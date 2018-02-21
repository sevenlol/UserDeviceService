package com.sevenloldev.spring.userdevice.device.type;

import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.*;

/**
 * {@link DeviceTypeRepository} implementation using generated JPA repository
 * interface
 */
@Repository
public class SpringDataDeviceTypeRepository implements DeviceTypeRepository {

  @Autowired
  private JpaDeviceTypeRepository repo;

  @Override
  public String create(DeviceType deviceType) {
    checkRequired(deviceType);

    DeviceType res = repo.save(deviceType);
    if (res.getType() == null) {
      // failed to auto generate type value
      throw new ServerErrorException();
    }

    // return as string for flexibility
    return res.getType().toString();
  }

  @Override
  public QueryResponse<DeviceType> query(DeviceTypeQuery query) {
    Specification<DeviceType> spec = getSpec(query);

    int page = query.getOffset() / query.getLimit();
    Pageable pageable = PageRequest.of(page, query.getLimit());
    try {
      Page<DeviceType> deviceTypes = repo.findAll(spec, pageable);
      // retrieve from page object
      List<DeviceType> result = new ArrayList<>();
      for (DeviceType type : deviceTypes) {
        result.add(type);
      }
      QueryResponse<DeviceType> response = new QueryResponse<>(
          // total device types that satisfy current query condition
          (int) deviceTypes.getTotalElements(),
          // current batch
          result
      );
      return response;
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
  }

  @Override
  public DeviceType get(String type) {
    int id = getType(type);
    Optional<DeviceType> result = null;
    try {
      result = repo.findById(id);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
    if (!result.isPresent()) {
      // device type not found
      throw new ResourceNotExistException();
    }
    return result.get();
  }

  @Override
  @Transactional
  public void update(String type, DeviceType deviceType) {
    // FIXME find a better way
    int id = getType(type);
    // check if there are fields for update and there is no invalid fields
    checkOptional(deviceType);

    try {
      Optional<DeviceType> result = repo.findById(id);
      if (!result.isPresent()) {
        // device type not found
        throw new EmptyResultDataAccessException(1);
      }
      DeviceType typeInDb = result.get();
      // merge new state into the existing ones
      if (deviceType.getName() != null) {
        typeInDb.setName(deviceType.getName());
      }
      if (deviceType.getDescription() != null) {
        typeInDb.setDescription(deviceType.getDescription());
      }
      if (deviceType.getModelname() != null) {
        typeInDb.setModelname(deviceType.getModelname());
      }
      if (deviceType.getManufacturer() != null) {
        typeInDb.setManufacturer(deviceType.getManufacturer());
      }
      repo.save(typeInDb);
    } catch (EmptyResultDataAccessException e) {
      // device type does not exist
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
  }

  @Override
  public void delete(String type) {
    int id = getType(type);
    try {
      repo.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      // device type does not exist
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
  }

  /**
   * Check if all necessary fields are present
   * @param type target device type object
   */
  private void checkRequired(DeviceType type) {
    checkNotNull(type);
    checkNotNull(type.getName());
    checkNotNull(type.getModelname());
    checkNotNull(type.getManufacturer());
  }

  /**
   * Check if there is at least one non-null field for update and there are no invalid fields
   * @param type target device type object
   */
  private void checkOptional(DeviceType type) {
    checkNotNull(type);
    checkArgument(type.getName() != null || type.getDescription() != null ||
      type.getModelname() != null || type.getManufacturer() != null);
    checkNonEmptyString(type.getName());
    checkNonEmptyString(type.getDescription());
    checkNonEmptyString(type.getModelname());
    checkNonEmptyString(type.getManufacturer());
  }

  /** string is non-empty (if present) */
  private void checkNonEmptyString(String s) {
    if (s != null) {
      checkArgument(!s.isEmpty());
    }
  }

  /**
   * Check if type is a string representing a valid number
   */
  private void checkType(String type) {
    checkNotNull(type);
    checkArgument(!type.isEmpty());
    try {
      Integer.parseInt(type);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException();
    }
  }

  /** retrieve integer value of the device type */
  private int getType(String type) {
    checkType(type);
    return Integer.parseInt(type);
  }

  /** generate JPA specification for querying device type */
  private Specification<DeviceType> getSpec(DeviceTypeQuery query) {
    check(query);
    return new DeviceTypeSpec(query);
  }

  /** check if the device type query object is valid */
  private void check(DeviceTypeQuery query) {
    checkNotNull(query);
    // NOTE trying out validator, not using DI
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<DeviceTypeQuery>> result =  validator.validate(query);
    checkNotNull(result);
    if (!result.isEmpty()) {
      throw new IllegalArgumentException();
    }
    // TODO use hibernate validator cross field validation
    // offset is multiple of limit
    checkArgument(query.getOffset() % query.getLimit() == 0);
  }

  /**
   * JPA Specification class for querying {@link DeviceType}
   */
  private class DeviceTypeSpec implements Specification<DeviceType> {
    private final DeviceTypeQuery query;

    public DeviceTypeSpec(DeviceTypeQuery query) {
      check(query);
      this.query = query;
    }

    @Nullable
    @Override
    public Predicate toPredicate(Root<DeviceType> root, CriteriaQuery<?> query,
        CriteriaBuilder cb) {
      List<Predicate> predicates = new ArrayList<>();
      // configure filtering predicate
      if (this.query.getName() != null) {
        predicates.add(cb.equal(root.get("name"), this.query.getName()));
      }
      if (this.query.getModelname() != null) {
        predicates.add(cb.equal(root.get("modelname"), this.query.getModelname()));
      }
      if (this.query.getManufacturer() != null) {
        predicates.add(cb.equal(root.get("manufacturer"), this.query.getManufacturer()));
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
}
