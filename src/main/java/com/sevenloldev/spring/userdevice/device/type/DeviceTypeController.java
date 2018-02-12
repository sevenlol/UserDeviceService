package com.sevenloldev.spring.userdevice.device.type;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceTypeController {
  @Autowired
  private DeviceTypeRepository repo;

  @PostMapping("/types/devices")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, String> createDeviceType(
      @Valid @RequestBody DeviceType deviceType, BindingResult result) {
    check(result);
    return getDeviceTypeResponse(repo.create(deviceType));
  }

  @GetMapping("/types/devices")
  public QueryResponse<DeviceType> queryDeviceTypes(
      @Valid  DeviceTypeQuery query, BindingResult result) {
    check(result);
    return repo.query(query);
  }

  @GetMapping("/types/devices/{type}")
  public DeviceType getDeviceTypeById(@PathVariable("type") String type) {
    return repo.get(type);
  }

  @PutMapping("/types/devices/{type}")
  public Map<String, String> updateDeviceType(
      @PathVariable("type") String type,
      @Valid @RequestBody DeviceType deviceType, BindingResult result) {
    check(result);
    repo.update(type, deviceType);
    return getDeviceTypeResponse(type);
  }

  @DeleteMapping("/types/devices/{type}")
  public Map<String, String> deleteDeviceType(@PathVariable("type") String type) {
    repo.delete(type);
    return getDeviceTypeResponse(type);
  }

  private void check(BindingResult result) {
    if (result.hasErrors()) {
      throw new IllegalArgumentException();
    }
  }

  private Map<String, String> getDeviceTypeResponse(String type) {
    Map<String, String> response = new HashMap<>();
    response.put("device_type", type);
    return response;
  }
}
