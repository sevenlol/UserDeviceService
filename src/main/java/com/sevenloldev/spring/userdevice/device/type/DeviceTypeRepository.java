package com.sevenloldev.spring.userdevice.device.type;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

public interface DeviceTypeRepository {
  String create(DeviceType deviceType);
  QueryResponse<DeviceType> query(DeviceTypeQuery query);
  DeviceType get(String type);
  void update(String type, DeviceType deviceType);
  void delete(String type);
}
