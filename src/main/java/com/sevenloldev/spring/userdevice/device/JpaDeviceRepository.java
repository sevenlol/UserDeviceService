package com.sevenloldev.spring.userdevice.device;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface JpaDeviceRepository extends CrudRepository<Device, Integer>,
    JpaSpecificationExecutor<Device> {

}
