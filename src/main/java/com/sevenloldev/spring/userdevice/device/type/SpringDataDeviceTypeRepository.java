package com.sevenloldev.spring.userdevice.device.type;

import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.*;

@Repository
public class SpringDataDeviceTypeRepository implements DeviceTypeRepository {

  @Autowired
  private JpaDeviceTypeRepository repo;

  @Override
  public String create(DeviceType deviceType) {
    checkRequired(deviceType);

    DeviceType res = repo.save(deviceType);
    if (res.getType() == null) {
      throw new ServerErrorException();
    }

    return res.getType().toString();
  }

  @Override
  public QueryResponse<DeviceType> query(DeviceTypeQuery query) {
    return null;
  }

  @Override
  public DeviceType get(String type) {
    int id = getType(type);
    Optional<DeviceType> result = null;
    try {
      result = repo.findById(id);
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerErrorException(e);
    }
    if (!result.isPresent()) {
      throw new ResourceNotExistException();
    }
    return result.get();
  }

  @Override
  @Transactional
  public void update(String type, DeviceType deviceType) {
    // FIXME find a better way
    int id = getType(type);
    checkOptional(deviceType);

    try {
      Optional<DeviceType> result = repo.findById(id);
      if (!result.isPresent()) {
        throw new EmptyResultDataAccessException(1);
      }
      DeviceType typeInDb = result.get();
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

  private void checkRequired(DeviceType type) {
    checkNotNull(type);
    checkNotNull(type.getName());
    checkNotNull(type.getModelname());
    checkNotNull(type.getManufacturer());
  }

  private void checkOptional(DeviceType type) {
    checkNotNull(type);
    checkArgument(type.getName() != null || type.getDescription() != null ||
      type.getModelname() != null || type.getManufacturer() != null);
    checkNonEmptyString(type.getName());
    checkNonEmptyString(type.getDescription());
    checkNonEmptyString(type.getModelname());
    checkNonEmptyString(type.getManufacturer());
  }

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

  private int getType(String type) {
    checkType(type);
    return Integer.parseInt(type);
  }
}
