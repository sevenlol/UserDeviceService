package com.sevenloldev.spring.userdevice.device.type;

import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

/**
 * Interface for managing {@link DeviceType} CRUD operations
 */
public interface DeviceTypeRepository {
  /**
   * Create a {@link DeviceType} entity
   * @param deviceType {@link DeviceType} object to be created (must contains name, description,
   * modelname, manufacturer name)
   * @return auto-generated device type
   * @throws IllegalArgumentException if the {@link DeviceType} object is invalid
   * @throws ServerErrorException if the operation failed
   */
  String create(DeviceType deviceType);
  /**
   * Retrieves {@link DeviceType} entities that conform the given {@link DeviceTypeQuery}
   * @param query query object for this retrieval (must contain limit,offset and sort)
   * @return query results (total device types that match the query and device type of this batch)
   * @throws IllegalArgumentException if query is invalid
   * @throws ServerErrorException if the operation failed
   */
  QueryResponse<DeviceType> query(DeviceTypeQuery query);
  /**
   * Retrieve {@link DeviceType} entity by device ID
   * @param type device type
   * @return retrieved {@link DeviceType} entity
   * @throws IllegalArgumentException if id is invalid
   * @throws ResourceNotExistException if {@link DeviceType} with specified ID does not exist
   * @throws ServerErrorException if the operation failed
   */
  DeviceType get(String type);
  /**
   * Update {@link DeviceType} of specified ID, valid fields include name, description,
   * modelname, manufacturer name
   * @param type device type
   * @param deviceType update device deviceType object (its fields will be merged
   * into the existing device)
   * @throws IllegalArgumentException if either device type or device type object is invalid
   * @throws ResourceNotExistException if {@link DeviceType} with specified type does not exist or
   * specified {@link DeviceType} does not exist
   * to be updated already exists
   * @throws ServerErrorException if the operation failed
   */
  void update(String type, DeviceType deviceType);
  /**
   * Delete {@link DeviceType} of specified ID
   * @param type device type
   * @throws IllegalArgumentException if device type is invalid
   * @throws ResourceNotExistException if {@link DeviceType} with specified type does not exist
   * @throws ServerErrorException if the operation failed
   */
  void delete(String type);
}
