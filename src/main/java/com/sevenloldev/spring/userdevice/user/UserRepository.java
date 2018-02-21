package com.sevenloldev.spring.userdevice.user;

import com.sevenloldev.spring.userdevice.util.error.ResourceExistException;
import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

/**
 * Interface for managing {@link User} CRUD operations
 */
public interface UserRepository {

  /**
   * Create a {@link User} entity
   * @param user {@link User} object to be created (must contains email,name,password field)
   * @return auto-generated user ID
   * @throws IllegalArgumentException if the user object is invalid
   * @throws ServerErrorException if the operation failed
   */
  String create(User user);

  /**
   * Retrieves {@link User} entities that conform the given {@link UserQuery}
   * @param query query object for this retrieval (must contain limit,offset and sortField)
   * @return query results (total users that match the query and users of this batch)
   * @throws IllegalArgumentException if query is invalid
   * @throws ServerErrorException if the operation failed
   */
  QueryResponse<User> query(UserQuery query);

  /**
   * Retrieve {@link User} entity by user ID
   * @param id user ID
   * @return retrieved {@link User} entity
   * @throws IllegalArgumentException if id is invalid
   * @throws ResourceNotExistException if {@link User} with specified ID does not exist
   * @throws ServerErrorException if the operation failed
   */
  User get(String id);

  /**
   * Update {@link User} of specified ID, valid fields include name, email and password
   * @param id user ID
   * @param user update user object (its fields will be merged into the existing user)
   * @throws IllegalArgumentException if id or user object is invalid
   * @throws ResourceNotExistException if {@link User} with specified ID does not exist
   * @throws ResourceExistException if a field (that should be unique) to be updated already exists
   * @throws ServerErrorException if the operation failed
   */
  void update(String id, User user);

  /**
   * Delete {@link User} of specified ID
   * @param id user ID
   * @throws IllegalArgumentException if id is invalid
   * @throws ResourceNotExistException if {@link User} with specified ID does not exist
   * @throws ServerErrorException if the operation failed
   */
  void delete(String id);
}
