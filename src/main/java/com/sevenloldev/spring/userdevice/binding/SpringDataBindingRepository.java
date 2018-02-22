package com.sevenloldev.spring.userdevice.binding;

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
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
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

import static com.google.common.base.Preconditions.*;

/**
 * {@link BindingRepository} implementation using generated JPA repository
 * interface
 */
@Repository
public class SpringDataBindingRepository implements BindingRepository {
  private static final String USER_DEVICE_UNIQUE_CONSTRAINT_NAME = "userDevice";

  @Autowired
  private JpaBindingRepository repo;

  @Override
  public String create(Binding binding) {
    checkRequired(binding);

    // set binding time
    binding.setBoundAt(LocalDateTime.now());

    Binding result = null;
    try {
      result = repo.save(binding);
    } catch (DataIntegrityViolationException e) {
      if (!(e.getCause() instanceof ConstraintViolationException)) {
        // unknown integrity violation
        throw new ServerErrorException(e);
      }

      ConstraintViolationException ce = (ConstraintViolationException) e.getCause();
      if (USER_DEVICE_UNIQUE_CONSTRAINT_NAME.equalsIgnoreCase(ce.getConstraintName())) {
        // userId - deviceId binding already exists
        throw new ResourceExistException(e);
      }

      // user id or device id does not exist
      throw new ResourceNotExistException(e);
    }
    if (result.getBindingId() == null) {
      // failed to generate binding id, should not happen
      throw new ServerErrorException();
    }
    return result.getBindingId();
  }

  @Override
  public QueryResponse<Binding> query(BindingQuery query) {
    Specification<Binding> spec = getSpec(query);
    int page = query.getOffset() / query.getLimit();
    Pageable pageable = PageRequest.of(page, query.getLimit());
    try {
      Page<Binding> result = repo.findAll(spec, pageable);
      List<Binding> bindings = new ArrayList<>();
      for (Binding binding : result) {
        // transform the query result and add to result list
        bindings.add(transform(binding, query.attachDevices()));
      }
      return new QueryResponse<>(
          (int) result.getTotalElements(),
          bindings
      );
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerErrorException(e);
    }
  }

  @Override
  public Binding get(String id) {
    int bindingId = getId(id);

    Optional<Binding> result = null;
    try {
      // get binding and device
      result = repo.getById(bindingId);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
    if (!result.isPresent()) {
      // binding with specified ID not found
      throw new ResourceNotExistException();
    }
    return transform(result.get(), true);
  }

  @Override
  public void update(String id, Binding binding) {
    // not supported
    throw new UnsupportedOperationException();
  }

  @Override
  public void delete(String id) {
    int bindingId = getId(id);
    try {
      repo.deleteById(bindingId);
    } catch (EmptyResultDataAccessException e) {
      // binding does not exist
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      throw new ServerErrorException(e);
    }
  }

  /** check if the binding object contains all required field */
  private void checkRequired(Binding binding) {
    checkNotNull(binding);
    // NOTE deviceId is set in device.id property
    checkNotNull(binding.getDevice());
    checkNotNull(binding.getDevice().getId());
    checkNotNull(binding.getUserId());
  }

  // retrieve numeral ID from string
  private int getId(String id) {
    checkNotNull(id);
    try {
      return Integer.parseInt(id);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * transform binding from query to a valid response
   * set deviceId field and clear out embedded device entity if necessary
   * @param binding binding object retrieved from JPA repository
   * @param attachDevice flag to determine whether to embed device object or not
   * @return valid binding response object
   */
  private Binding transform(Binding binding, boolean attachDevice) {
    checkNotNull(binding);

    // set deviceId
    if (binding.getDevice() != null) {
      binding.setDeviceId(binding.getDevice().getId());
    }

    // clear device (proxy object)
    if (!attachDevice) {
      binding.setDevice(null);
    }

    return binding;
  }

  /** generate JPA specification from binding query */
  private Specification<Binding> getSpec(BindingQuery query) {
    check(query);
    return new BindingSpec(query);
  }

  /** check if the binding query is valid */
  private void check(BindingQuery query) {
    checkNotNull(query);
    // NOTE trying out validator, not using DI
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<BindingQuery>> result =  validator.validate(query);
    checkNotNull(result);
    if (!result.isEmpty()) {
      throw new IllegalArgumentException();
    }
    // TODO use hibernate validator cross field validation
    // offset is multiple of limit
    checkArgument(query.getOffset() % query.getLimit() == 0);
  }

  /**
   * JPA Specification class for querying {@link Binding}
   */
  private class BindingSpec implements Specification<Binding> {
    private final BindingQuery query;

    public BindingSpec(BindingQuery query) {
      checkNotNull(query);
      this.query = query;
    }

    @Override
    public Predicate toPredicate(Root<Binding> root, CriteriaQuery<?> query,
        CriteriaBuilder cb) {
      if (this.query.attachDevices()) {
        // join device
        if (query.getResultType() != Long.class && query.getResultType() != long.class) {
          // only fetch join if the query is not "count"
          root.fetch("device", JoinType.INNER);
        }
      }
      return configurePredicates(root, query, cb);
    }

    private Predicate configurePredicates(From<?, ?> root, CriteriaQuery<?> query,
        CriteriaBuilder cb) {
      List<Predicate> predicates = new ArrayList<>();
      // configure filtering predicate
      if (this.query.getUserId() != null) {
        predicates.add(cb.equal(root.get("userId"), this.query.getUserId()));
      }
      if (this.query.getDeviceId() != null) {
        predicates.add(cb.equal(root.get("device").get("id"), this.query.getDeviceId()));
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
