package com.sevenloldev.spring.userdevice.binding;

import com.sevenloldev.spring.userdevice.util.error.ResourceExistException;
import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import static com.google.common.base.Preconditions.*;

@Repository
public class SpringDataBindingRepository implements BindingRepository {
  private static final String USER_DEVICE_UNIQUE_CONSTRAINT_NAME = "userDevice";

  @Autowired
  private JpaBindingRepository repo;

  @Override
  public String create(Binding binding) {
    checkRequired(binding);

    binding.setBoundAt(LocalDateTime.now());

    Binding result = null;
    try {
      result = repo.save(binding);
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
      if (!(e.getCause() instanceof ConstraintViolationException)) {
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
    return null;
  }

  @Override
  public Binding get(String id) {
    int bindingId = getId(id);

    Optional<Binding> result = null;
    try {
      // get binding and device
      result = repo.getById(bindingId);
    } catch (Exception e) {
      throw new ServerErrorException(e);
    }
    if (!result.isPresent()) {
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

  private void checkRequired(Binding binding) {
    checkNotNull(binding);
    checkNotNull(binding.getDevice());
    checkNotNull(binding.getDevice().getId());
    checkNotNull(binding.getUserId());
  }

  private int getId(String id) {
    checkNotNull(id);
    try {
      return Integer.parseInt(id);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(e);
    }
  }

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
}
