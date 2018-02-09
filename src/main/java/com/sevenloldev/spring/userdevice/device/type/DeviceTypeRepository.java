package com.sevenloldev.spring.userdevice.device.type;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

public interface DeviceTypeRepository {
  String create(DeviceType type);
  QueryResponse<DeviceType> query(DeviceTypeQuery query);
  DeviceType get(String id);
  void update(String id, DeviceType type);
  void delete(String id);
}
