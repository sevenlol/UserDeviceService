package com.sevenloldev.spring.userdevice.device;

import com.sevenloldev.spring.userdevice.device.type.DeviceType;
import com.sevenloldev.spring.userdevice.util.error.ResourceExistException;
import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.*;

@Repository
public class SpringDataDeviceRepository implements DeviceRepository {
  private static final String UNIQUE_MAC_CONSTRAINT_NAME = "mac";

  @Autowired
  private JpaDeviceRepository repo;

  @Override
  public String create(Device device) {
    checkRequired(device);

    // set time properties
    device.setCreatedAt(LocalDateTime.now());
    device.setUpdatedAt(LocalDateTime.now());

    // normalize mac address
    device.setMac(normalizeMac(device.getMac()));

    // set device type
    DeviceType type = new DeviceType();
    type.setType(device.getType());
    device.setDeviceType(type);

    Device result;
    try {
      result = repo.save(device);
    } catch (DataIntegrityViolationException e) {
      throw handleIntegrityViolationException(e);
    } catch (Exception e) {
      throw new ServerErrorException(e);
    }
    if (result.getId() == null) {
      throw new ServerErrorException();
    }
    return result.getId().toString();
  }

  @Override
  public QueryResponse<Device> query(DeviceQuery query) {
    Specification<Device> spec = getSpec(query);
    int page = query.getOffset() / query.getLimit();
    Pageable pageable = PageRequest.of(page, query.getLimit());
    try {
      Page<Device> devices = repo.findAll(spec, pageable);
      List<Device> result = new ArrayList<>();
      for (Device type : devices) {
        result.add(type);
      }
      QueryResponse<Device> response = new QueryResponse<>(
          (int) devices.getTotalElements(),
          result
      );
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerErrorException(e);
    }
  }

  @Override
  public Device get(String id) {
    int deviceId = getId(id);
    Optional<Device> result;
    try {
      result = repo.findById(deviceId);
    } catch (Exception e) {
      throw new ServerErrorException(e);
    }
    if (!result.isPresent()) {
      throw new ResourceNotExistException();
    }
    return processDeviceResponse(result.get());
  }

  @Override
  public void update(String id, Device device) {
    int deviceId = getId(id);
    checkOptional(device);

    try {
      getNUpdate(deviceId, device);
    } catch (DataIntegrityViolationException e) {
      // mac already exist or device type does not exist
      throw handleIntegrityViolationException(e);
    }
  }

  @Override
  public void delete(String id) {
    int deviceId = getId(id);
    try {
      repo.deleteById(deviceId);
    } catch (EmptyResultDataAccessException e) {
      // device does not exist
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
  }

  @Transactional
  private void getNUpdate(int deviceId, Device device) {
    try {
      Optional<Device> result = repo.findById(deviceId);
      if (!result.isPresent()) {
        throw new EmptyResultDataAccessException(1);
      }
      Device deviceInDb = result.get();
      if (device.getType() != null) {
        // set device type
        DeviceType type = new DeviceType();
        type.setType(device.getType());
        deviceInDb.setDeviceType(type);
        deviceInDb.setType(device.getType());
      }
      if (device.getName() != null) {
        deviceInDb.setName(device.getName());
      }
      if (device.getMac() != null) {
        // normalize mac address
        deviceInDb.setMac(normalizeMac(device.getMac()));
      }
      if (device.getPinCode() != null) {
        deviceInDb.setPinCode(device.getPinCode());
      }
      deviceInDb.setUpdatedAt(LocalDateTime.now());
      repo.save(deviceInDb);
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      throw e;
    } catch (EmptyResultDataAccessException e) {
      // device does not exist
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
  }

  private void checkRequired(Device device) {
    checkNotNull(device);
    checkNotNull(device.getMac());
    checkNotNull(device.getType());
    checkNotNull(device.getPinCode());
  }

  private void checkOptional(Device device) {
    checkNotNull(device);
    checkArgument(device.getMac() != null || device.getName() != null ||
      device.getPinCode() != null || device.getType() != null);
    checkNonEmptyString(device.getMac());
    checkNonEmptyString(device.getName());
  }

  private void checkNonEmptyString(String s) {
    if (s != null) {
      checkArgument(!s.isEmpty());
    }
  }

  private void checkId(String id) {
    try {
      Integer.parseInt(id);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private Specification<Device> getSpec(DeviceQuery query) {
    check(query);
    return new DeviceSpec(query);
  }

  private void check(DeviceQuery query) {
    checkNotNull(query);
    // NOTE trying out validator, not using DI
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<DeviceQuery>> result =  validator.validate(query);
    checkNotNull(result);
    if (!result.isEmpty()) {
      throw new IllegalArgumentException();
    }
    // TODO use hibernate validator cross field validation
    // offset is multiple of limit
    checkArgument(query.getOffset() % query.getLimit() == 0);
  }

  private int getId(String id) {
    checkId(id);
    return Integer.parseInt(id);
  }

  private Device processDeviceResponse(Device device) {
    checkNotNull(device);
    checkNotNull(device.getDeviceType());
    device.setType(device.getDeviceType().getType());
    return device;
  }

  private RuntimeException handleIntegrityViolationException(DataIntegrityViolationException e) {
    if (!(e.getCause() instanceof ConstraintViolationException)) {
      // not recognized error
      return new ServerErrorException(e);
    }

    ConstraintViolationException ce = (ConstraintViolationException) e.getCause();
    String constraint = ce.getConstraintName();
    if (UNIQUE_MAC_CONSTRAINT_NAME.equalsIgnoreCase(constraint)) {
      // duplicate mac
      return new ResourceExistException(e);
    } else if (constraint == null) {
      // TODO find a better way to separate foreign key violation
      // device type does not exist
      return new ResourceNotExistException(e);
    }
    return new ServerErrorException(e);
  }

  private class DeviceSpec implements Specification<Device> {
    private final DeviceQuery query;

    public DeviceSpec(DeviceQuery query) {
      checkNotNull(query);
      this.query = query;
    }

    @Override
    public Predicate toPredicate(Root<Device> root, CriteriaQuery<?> query,
        CriteriaBuilder cb) {
      List<Predicate> predicates = new ArrayList<>();
      // configure filtering predicate
      if (this.query.getMac() != null) {
        predicates.add(cb.equal(root.get("mac"), normalizeMac(this.query.getMac())));
      }
      if (this.query.getName() != null) {
        predicates.add(cb.equal(root.get("name"), this.query.getName()));
      }
      if (this.query.getType() != null) {
        predicates.add(cb.equal(root.get("type"), this.query.getType()));
      }

      // configure order by (in criteria query)
      String sort = this.query.getSort();
      boolean asc = true;
      if (sort.startsWith("-")) {
        asc = false;
        sort = sort.substring(1);
      }
      if (asc) {
        query.orderBy(cb.asc(root.get(sort)));
      } else {
        query.orderBy(cb.desc(root.get(sort)));
      }
      return cb.and(predicates.toArray(new Predicate[] {}));
    }
  }

  private String normalizeMac(String mac) {
    return mac.toLowerCase();
  }
}
