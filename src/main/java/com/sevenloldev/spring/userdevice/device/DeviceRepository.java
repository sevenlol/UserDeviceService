package com.sevenloldev.spring.userdevice.device;

import com.sevenloldev.spring.userdevice.device.type.DeviceType;
import com.sevenloldev.spring.userdevice.util.error.ResourceExistException;
import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

/**
 * Interface for managing {@link Device} CRUD operations
 */
public interface DeviceRepository {
  /**
   * Create a {@link Device} entity
   * @param device {@link Device} object to be created (must contains name, mac, pinCode, type)
   * @return auto-generated device ID
   * @throws IllegalArgumentException if the device object is invalid
   * @throws ResourceNotExistException if the specified {@link DeviceType} does not exist
   * @throws ResourceExistException if another device has the same mac address
   * @throws ServerErrorException if the operation failed
   */
  String create(Device device);
  /**
   * Retrieves {@link Device} entities that conform the given {@link DeviceQuery}
   * @param query query object for this retrieval (must contain limit,offset and sort)
   * @return query results (total devices that match the query and devices of this batch)
   * @throws IllegalArgumentException if query is invalid
   * @throws ServerErrorException if the operation failed
   */
  QueryResponse<Device> query(DeviceQuery query);
  /**
   * Retrieve {@link Device} entity by device ID
   * @param id device ID
   * @return retrieved {@link Device} entity
   * @throws IllegalArgumentException if id is invalid
   * @throws ResourceNotExistException if {@link Device} with specified ID does not exist
   * @throws ServerErrorException if the operation failed
   */
  Device get(String id);
  /**
   * Update {@link Device} of specified ID, valid fields include mac, name, type, pinCode
   * @param id device ID
   * @param device update device object (its fields will be merged into the existing device)
   * @throws IllegalArgumentException if id or device object is invalid
   * @throws ResourceNotExistException if {@link Device} with specified ID does not exist or
   * specified {@link DeviceType} does not exist
   * @throws ResourceExistException if another device with the mac address (that should be unique)
   * to be updated already exists
   * @throws ServerErrorException if the operation failed
   */
  void update(String id, Device device);
  /**
   * Delete {@link Device} of specified ID
   * @param id device ID
   * @throws IllegalArgumentException if id is invalid
   * @throws ResourceNotExistException if {@link Device} with specified ID does not exist
   * @throws ServerErrorException if the operation failed
   */
  void delete(String id);
}
