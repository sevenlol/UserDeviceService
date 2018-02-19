package com.sevenloldev.spring.userdevice.binding;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BindingController {

  @Autowired
  private BindingRepository repo;

  @PostMapping("/bindings")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, String> createBinding(
      @Validated(value = { Required.class }) @RequestBody Binding binding, BindingResult result) {
    System.out.println(result.getAllErrors());
    check(result);
    return getBindingIdResponse(repo.create(binding));
  }

  @GetMapping("/bindings")
  public QueryResponse<Binding> queryBindings(@Valid BindingQuery query, BindingResult result) {
    System.out.println(query.getDeviceId());
    check(result);
    return repo.query(query);
  }

  @GetMapping("/bindings/{id}")
  public Binding getBindingById(@PathVariable("id") String id) {
    return repo.get(id);
  }

  @DeleteMapping("/bindings/{id}")
  public Map<String, String> deleteBinding(@PathVariable("id") String id) {
    repo.delete(id);
    return getBindingIdResponse(id);
  }

  private void check(BindingResult result) {
    if (result.hasErrors()) {
      throw new IllegalArgumentException();
    }
  }

  private Map<String, String> getBindingIdResponse(String id) {
    Map<String, String> response = new HashMap<>();
    response.put("binding_id", id);
    return response;
  }
}
