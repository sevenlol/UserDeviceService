package com.sevenloldev.spring.userdevice.device.type;

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
 * Entry point for {@link DeviceType} related APIs
 */
@RestController
public class DeviceTypeController {
  private Logger logger = LoggerFactory.getLogger(DeviceTypeController.class);

  @Autowired
  private DeviceTypeRepository repo;

  /** Create Device Type API */
  @PostMapping("/types/devices")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, String> createDeviceType(
      @Validated(value = { Required.class }) @RequestBody DeviceType deviceType,
      BindingResult result) {
    check(result);
    String type = repo.create(deviceType);
    logger.info("DeviceType created, type={}", type);
    return getDeviceTypeResponse(type);
  }

  /** Query Device Type API */
  @GetMapping("/types/devices")
  public QueryResponse<DeviceType> queryDeviceTypes(
      @Valid  DeviceTypeQuery query, BindingResult result) {
    check(result);
    QueryResponse<DeviceType> response = repo.query(query);
    logger.info("DeviceType query succeeded, size={}, total={}",
        response.getResults().size(), response.getTotal());
    return response;
  }

  /** Retrieve Device Type by Type API */
  @GetMapping("/types/devices/{type}")
  public DeviceType getDeviceTypeById(@PathVariable("type") String type) {
    DeviceType deviceType = repo.get(type);
    logger.info("Retrieved DeviceType={}", deviceType);
    return deviceType;
  }

  /** Device Type Full Update API */
  @PutMapping("/types/devices/{type}")
  public Map<String, String> updateDeviceType(
      @PathVariable("type") String type,
      @Validated(value = { Required.class }) @RequestBody DeviceType deviceType,
      BindingResult result) {
    check(result);
    repo.update(type, deviceType);
    logger.info("DeviceType(type={}) updated, updated device type={}", type, deviceType);
    return getDeviceTypeResponse(type);
  }

  /** Device Type Partial Update API */
  @PatchMapping("/types/devices/{type}")
  public Map<String, String> partiallyUpdateDeviceType(
      @PathVariable("type") String type,
      @Validated(value = { Optional.class }) @RequestBody DeviceType deviceType,
      BindingResult result) {
    check(result);
    repo.update(type, deviceType);
    logger.info("DeviceType(type={}) partially updated, update state={}", type, deviceType);
    return getDeviceTypeResponse(type);
  }

  /** Delete Device Type API */
  @DeleteMapping("/types/devices/{type}")
  public Map<String, String> deleteDeviceType(@PathVariable("type") String type) {
    repo.delete(type);
    logger.info("DeviceType(type={}) deleted", type);
    return getDeviceTypeResponse(type);
  }

  /** helper for checking validation result */
  private void check(BindingResult result) {
    if (result.hasErrors()) {
      logger.debug(result.getAllErrors().toString());
      throw new IllegalArgumentException();
    }
  }

  /** helper for generating response */
  private Map<String, String> getDeviceTypeResponse(String type) {
    Map<String, String> response = new HashMap<>();
    response.put("device_type", type);
    return response;
  }
}
