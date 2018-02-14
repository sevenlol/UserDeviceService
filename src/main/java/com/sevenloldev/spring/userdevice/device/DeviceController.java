package com.sevenloldev.spring.userdevice.device;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceController {

  @Autowired
  private DeviceRepository repo;

  @PostMapping("/devices")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, String> createDevice(
      @Validated(Required.class) @RequestBody Device device, BindingResult result) {
    check(result);
    return getDeviceResponse(repo.create(device));
  }

  @GetMapping("/devices")
  public QueryResponse<Device> queryDevices(
      @Valid DeviceQuery query, BindingResult result) {
    check(result);
    return repo.query(query);
  }

  @GetMapping("/devices/{id}")
  public Device getDeviceById(@PathVariable("id") String id) {
    return repo.get(id);
  }

  @PutMapping("/devices/{id}")
  public Map<String, String> updateDevice(
      @PathVariable("id") String id,
      @Validated(Optional.class) @RequestBody Device device, BindingResult result) {
    check(result);
    repo.update(id, device);
    return getDeviceResponse(id);
  }

  @DeleteMapping("/devices/{id}")
  public Map<String, String> deleteDevice(@PathVariable("id") String id) {
    repo.delete(id);
    return getDeviceResponse(id);
  }

  private Map<String, String> getDeviceResponse(String deviceId) {
    Map<String, String> response = new HashMap<>();
    response.put("device_id", deviceId);
    return response;
  }

  private void check(BindingResult result) {
    if (result.hasErrors()) {
      throw new IllegalArgumentException();
    }
  }
}
