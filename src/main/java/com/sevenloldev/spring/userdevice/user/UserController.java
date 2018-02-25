package com.sevenloldev.spring.userdevice.user;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
import com.sevenloldev.spring.userdevice.util.validation.Optional;
import com.sevenloldev.spring.userdevice.util.validation.Required;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Entry point for {@link User} related APIs
 */
@RestController
public class UserController {
  private final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserRepository repo;

  /** Create User API */
  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, String> createUser(
      @Validated(Required.class) @RequestBody User user, BindingResult result) {
    check(result);
    String userId = repo.create(user);
    logger.info("User created, ID={}", userId);
    return getUserIdResponse(userId);
  }

  /** Query User API */
  @GetMapping("/users")
  public QueryResponse<User> queryUsers(
      @Valid UserQueryRequest req, BindingResult result) {
    check(result);
    QueryResponse<User> response = repo.query(new UserQuery(req));
    logger.info("User query succeeded, size={}, total={}",
        response.getResults().size(), response.getTotal());
    return response;
  }

  /** Get User by ID API */
  @GetMapping("/users/{id}")
  public User getUserById(@PathVariable("id") String id) {
    User user = repo.get(id);
    logger.info("Retrieved user={}", user);
    return user;
  }

  /** Update User API */
  @PutMapping("/users/{id}")
  public Map<String, String> updateUser(
      @PathVariable("id") String id,
      @Validated(Required.class) @RequestBody User user,
      BindingResult result) {
    check(result);
    repo.update(id, user);
    logger.info("User(id={}) updated, updated user: {}", id, user);
    return getUserIdResponse(id);
  }

  /** Partial Update User API */
  @PatchMapping("/users/{id}")
  public Map<String, String> partiallyUpdateUser(
      @PathVariable("id") String id,
      @Validated(Optional.class) @RequestBody User user,
      BindingResult result) {
    check(result);
    repo.update(id, user);
    logger.info("User(id={}) partially updated, update state: {}", id, user);
    return getUserIdResponse(id);
  }

  /** Delete User API */
  @DeleteMapping("/users/{id}")
  public Map<String, String> deleteUser(
      @PathVariable("id") String id) {
    repo.delete(id);
    logger.info("User(id={}) deleted", id);
    return getUserIdResponse(id);
  }

  private void check(BindingResult result) {
    if (result.hasErrors()) {
      logger.debug(result.getAllErrors().toString());
      throw new IllegalArgumentException();
    }
  }

  private Map<String, String> getUserIdResponse(String id) {
    Map<String, String> response = new HashMap<>();
    response.put("user_id", id);
    return response;
  }
}
