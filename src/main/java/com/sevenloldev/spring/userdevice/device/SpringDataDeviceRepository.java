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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.*;

/**
 * {@link DeviceRepository} implementation using generated JPA repository
 * interface
 */
@Repository
public class SpringDataDeviceRepository implements DeviceRepository {
  private final Logger logger = LoggerFactory.getLogger(SpringDataDeviceRepository.class);

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

    logger.debug("Create Device object = {}", device);

    Device result;
    try {
      result = repo.save(device);
    } catch (DataIntegrityViolationException e) {
      // duplicate mac address or invalid device type
      logger.error("Failed to create Device due to duplicate MAC address" +
          " or invalid type value, error={}", e.getMessage());
      logger.debug("Error=", e);
      throw handleIntegrityViolationException(e);
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to create Device, error={}", e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }
    if (result.getId() == null) {
      // failed to generate device ID
      logger.error("Failed to generate device ID");
      throw new ServerErrorException();
    }
    logger.debug("Device created, device={}", result);
    return result.getId().toString();
  }

  @Override
  public QueryResponse<Device> query(DeviceQuery query) {
    Specification<Device> spec = getSpec(query);

    logger.debug("Query={}, spec={}", query, spec);

    int page = query.getOffset() / query.getLimit();
    Pageable pageable = PageRequest.of(page, query.getLimit());
    try {
      Page<Device> devices = repo.findAll(spec, pageable);
      List<Device> result = new ArrayList<>();
      for (Device type : devices) {
        // set the device type for response
        // only the type (not retrieving the entire DeviceType object)
        type.setType(type.getDeviceType().getType());
        result.add(type);
      }
      logger.debug("Devices={}, total={}", result, devices.getTotalElements());
      QueryResponse<Device> response = new QueryResponse<>(
          // total count that matches the spec (query)
          (int) devices.getTotalElements(),
          // current batch
          result
      );
      return response;
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to query Device, error={}", e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }
  }

  @Cacheable("devices")
  @Override
  public Device get(String id) {
    int deviceId = getId(id);
    Optional<Device> result;
    try {
      result = repo.findById(deviceId);
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to retrieve Deivce(ID={})", id);
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }
    if (!result.isPresent()) {
      // no device associated with the given ID
      logger.debug("Device(ID={}) does not exist", id);
      throw new ResourceNotExistException();
    }
    Device response = processDeviceResponse(result.get());
    logger.debug("Retrieved Device={}", response);
    return response;
  }

  @CacheEvict(cacheNames = "devices", key = "#id")
  @Override
  public void update(String id, Device device) {
    int deviceId = getId(id);
    // check if new state has invalid property
    checkOptional(device);

    logger.debug("Update Device(ID={}), state={}", id, device);

    try {
      // retrieve and update
      // TODO generate update query dynamically
      getNUpdate(deviceId, device);
      logger.debug("Device(ID={}) updated successfully");
    } catch (DataIntegrityViolationException e) {
      // mac already exist or device type does not exist
      logger.error("Failed to update Device(ID={}), error={}", id, e.getMessage());
      logger.debug("Error=", e);
      throw handleIntegrityViolationException(e);
    }
  }

  @CacheEvict(cacheNames = "devices", key = "#id")
  @Override
  public void delete(String id) {
    int deviceId = getId(id);
    try {
      repo.deleteById(deviceId);
    } catch (EmptyResultDataAccessException e) {
      // device does not exist
      logger.error("Device(ID={}) does not exist", id);
      logger.debug("Error=", e);
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to delete Device(ID={}), error={}", id, e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }
  }

  /**
   * Helper method to retrieve the device, merge its fields with the new state
   * and save to the database atomically
   * @param deviceId ID of the target device, should not be {@literal null}
   * @param device new state to be merged into the current one, should have at
   * least one update field
   */
  @Transactional
  private void getNUpdate(int deviceId, Device device) {
    try {
      Optional<Device> result = repo.findById(deviceId);
      if (!result.isPresent()) {
        // device with given ID does not exist
        throw new EmptyResultDataAccessException(1);
      }
      Device deviceInDb = result.get();
      if (device.getType() != null) {
        // configure device type
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
      // set update time
      deviceInDb.setUpdatedAt(LocalDateTime.now());
      repo.save(deviceInDb);
    } catch (DataIntegrityViolationException e) {
      // new mac already exist or new device type does not exist
      throw e;
    } catch (EmptyResultDataAccessException e) {
      // device does not exist
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
  }

  /**
   * Check if {@link Device} object contains all required field
   * @param device device object to be checked
   */
  private void checkRequired(Device device) {
    checkNotNull(device);
    checkNotNull(device.getMac());
    checkNotNull(device.getType());
    checkNotNull(device.getPinCode());
  }

  /**
   * Check if {@link Device} object has at least one field and all fields
   * are valid
   * @param device device object to be checked
   */
  private void checkOptional(Device device) {
    checkNotNull(device);
    checkArgument(device.getMac() != null || device.getName() != null ||
      device.getPinCode() != null || device.getType() != null);
    checkNonEmptyString(device.getMac());
    checkNonEmptyString(device.getName());
  }

  /** helper methods for checking if the input string is non-empty (if present) */
  private void checkNonEmptyString(String s) {
    if (s != null) {
      checkArgument(!s.isEmpty());
    }
  }

  /** check if the id is a valid number */
  private void checkId(String id) {
    try {
      Integer.parseInt(id);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /** helper method for generate specification from device query */
  private Specification<Device> getSpec(DeviceQuery query) {
    check(query);
    return new DeviceSpec(query);
  }

  /** validate the device query */
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

  /** generate deviceId */
  private int getId(String id) {
    checkId(id);
    return Integer.parseInt(id);
  }

  /** modfiy device object for response */
  private Device processDeviceResponse(Device device) {
    checkNotNull(device);
    checkNotNull(device.getDeviceType());
    device.setType(device.getDeviceType().getType());
    device.setDeviceType(null);
    return device;
  }

  /**
   * Handle event that violate the database schema constraints
   * @param e target event
   * @return application specific exception object, {@link ResourceNotExistException} if foreign key
   * violation (related entity does not exist), {@link ResourceExistException} if unique key
   * violation, otherwise {@link ServerErrorException}
   */
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

  /**
   * JPA Specification class for querying {@link Device}
   */
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

  /**
   * Normalize mac address before storing to database
   * @param mac target mac address
   * @return normalized mac address
   */
  private String normalizeMac(String mac) {
    return mac.toLowerCase();
  }
}
