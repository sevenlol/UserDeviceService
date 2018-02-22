package com.sevenloldev.spring.userdevice.binding;

import com.sevenloldev.spring.userdevice.device.Device;
import com.sevenloldev.spring.userdevice.user.User;
import com.sevenloldev.spring.userdevice.util.error.ResourceExistException;
import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

/**
 * Interface for managing {@link Binding} CRUD operations
 */
public interface BindingRepository {
  /**
   * Create a {@link Binding} entity
   * @param binding {@link Binding} object to be created (must contains userId, deviceId)
   * @return auto-generated binding ID
   * @throws IllegalArgumentException if the binding object is invalid
   * @throws ResourceNotExistException if referenced {@link User} or {@link Device} does not exist
   * @throws ResourceExistException if another binding with the same (userId, deviceId) pair exists
   * @throws ServerErrorException if the operation failed
   */
  String create(Binding binding);
  /**
   * Retrieves {@link Binding} entities that conform the given {@link BindingQuery}
   * @param query query object for this retrieval (must contain limit,offset and sort)
   * @return query results (total bindings that match the query and bindings of this batch)
   * @throws IllegalArgumentException if query is invalid
   * @throws ServerErrorException if the operation failed
   */
  QueryResponse<Binding> query(BindingQuery query);
  /**
   * Retrieve {@link Binding} entity embedded with {@link Device} by binding ID
   * @param id binding ID
   * @return retrieved {@link Binding} entity
   * @throws IllegalArgumentException if id is invalid
   * @throws ResourceNotExistException if {@link Binding} with specified ID does not exist
   * @throws ServerErrorException if the operation failed
   */
  Binding get(String id);
  /**
   * Update {@link Binding} of specified ID, valid fields include userId, deviceId
   * @param id binding ID
   * @param binding update binding object (its fields will be merged into the existing binding)
   * @throws IllegalArgumentException if id or binding object is invalid
   * @throws ResourceNotExistException if {@link Binding} with specified ID does not exist or
   * specified {@link Device} or {@link User} does not exist
   * @throws ResourceExistException if another binding with the same (userId, deviceId) pair
   * (that should be unique) to be updated already exists
   * @throws ServerErrorException if the operation failed
   */
  void update(String id, Binding binding);
  /**
   * Delete {@link Binding} of specified ID
   * @param id binding ID
   * @throws IllegalArgumentException if id is invalid
   * @throws ResourceNotExistException if {@link Binding} with specified ID does not exist
   * @throws ServerErrorException if the operation failed
   */
  void delete(String id);
}
