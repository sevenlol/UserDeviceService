package com.sevenloldev.spring.userdevice.device.type;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Interface for spring to generate JPA repository
 */
public interface JpaDeviceTypeRepository extends CrudRepository<DeviceType, Integer>,
    JpaSpecificationExecutor<DeviceType> {
}
