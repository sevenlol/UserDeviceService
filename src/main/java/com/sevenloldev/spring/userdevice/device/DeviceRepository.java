package com.sevenloldev.spring.userdevice.device;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

public interface DeviceRepository {
  String create(Device device);
  QueryResponse<Device> query(DeviceQuery query);
  Device get(String id);
  void update(String id, Device device);
  void delete(String id);
}
