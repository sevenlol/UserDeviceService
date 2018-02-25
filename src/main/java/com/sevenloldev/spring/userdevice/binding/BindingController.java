package com.sevenloldev.spring.userdevice.binding;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BindingController {
  private Logger logger = LoggerFactory.getLogger(BindingController.class);

  @Autowired
  private BindingRepository repo;

  /** Create Binding (User-Device) API */
  @PostMapping("/bindings")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, String> createBinding(
      @Validated(value = { Required.class }) @RequestBody Binding binding,
      BindingResult result) {
    check(result);
    String id = repo.create(binding);
    logger.info("Binding created, ID={}, Binding={}", id, binding);
    return getBindingIdResponse(id);
  }

  /** Query Binding API */
  @GetMapping("/bindings")
  public QueryResponse<Binding> queryBindings(
      @Valid BindingQuery query, BindingResult result) {
    check(result);
    QueryResponse<Binding> response = repo.query(query);
    logger.info("Binding query={} succeeded, size={}, total={}",
        query, response.getResults().size(), response.getTotal());
    return response;
  }

  /** Get Binding by ID API */
  @GetMapping("/bindings/{id}")
  public Binding getBindingById(@PathVariable("id") String id) {
    Binding binding = repo.get(id);
    logger.info("Retrieved Binding={}", binding);
    return binding;
  }

  /** Delete Binding API */
  @DeleteMapping("/bindings/{id}")
  public Map<String, String> deleteBinding(@PathVariable("id") String id) {
    repo.delete(id);
    logger.info("Binding(ID={}) deleted", id);
    return getBindingIdResponse(id);
  }

  /** helper for checking validation result */
  private void check(BindingResult result) {
    if (result.hasErrors()) {
      logger.debug(result.getAllErrors().toString());
      throw new IllegalArgumentException();
    }
  }

  /** helper for generating response */
  private Map<String, String> getBindingIdResponse(String id) {
    Map<String, String> response = new HashMap<>();
    response.put("binding_id", id);
    return response;
  }
}
