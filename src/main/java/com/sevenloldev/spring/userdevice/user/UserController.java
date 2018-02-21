package com.sevenloldev.spring.userdevice.user;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
import com.sevenloldev.spring.userdevice.util.validation.Optional;
import com.sevenloldev.spring.userdevice.util.validation.Required;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
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
  @Autowired
  private UserRepository repo;

  /** Create User API */
  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, String> createUser(
      @Validated(Required.class) @RequestBody User user, BindingResult result) {
    check(result);
    return getUserIdResponse(repo.create(user));
  }

  /** Query User API */
  @GetMapping("/users")
  public QueryResponse<User> queryUsers(
      @Valid UserQueryRequest req, BindingResult result) {
    check(result);
    return repo.query(new UserQuery(req));
  }

  /** Get User by ID API */
  @GetMapping("/users/{id}")
  public User getUserById(@PathVariable("id") String id) {
    return repo.get(id);
  }

  /** Update User API */
  @PutMapping("/users/{id}")
  public Map<String, String> updateUser(
      @PathVariable("id") String id,
      @Validated(Required.class) @RequestBody User user,
      BindingResult result) {
    check(result);
    repo.update(id, user);
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
    return getUserIdResponse(id);
  }

  /** Delete User API */
  @DeleteMapping("/users/{id}")
  public Map<String, String> deleteUser(
      @PathVariable("id") String id) {
    repo.delete(id);
    return getUserIdResponse(id);
  }

  private void check(BindingResult result) {
    if (result.hasErrors()) {
      throw new IllegalArgumentException();
    }
  }

  private Map<String, String> getUserIdResponse(String id) {
    Map<String, String> response = new HashMap<>();
    response.put("user_id", id);
    return response;
  }
}
