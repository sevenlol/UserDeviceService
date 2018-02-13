package com.sevenloldev.spring.userdevice.device;

import com.sevenloldev.spring.userdevice.device.type.DeviceType;
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
    device.setMac(device.getMac().toLowerCase());

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
    return null;
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

  private void checkRequired(Device device) {
    checkNotNull(device);
    checkNotNull(device.getMac());
    checkNotNull(device.getType());
    checkNotNull(device.getPinCode());
  }

  private void checkId(String id) {
    try {
      Integer.parseInt(id);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(e);
    }
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
}
