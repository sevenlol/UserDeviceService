package com.sevenloldev.spring.userdevice.device;

import java.time.LocalDateTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Entity describing a device
 */
public class Device {
  /** unique device ID */
  private String id;

  /** device type */
  @NotEmpty
  private String type;

  /** mac address */
  @NotNull
  @Pattern(regexp = "^[a-fA-F0-9]{12}$")
  private String mac;

  /** pin code */
  @NotNull
  @Min(0)
  @Max(9999)
  private Integer pinCode;

  /** the time this device is created */
  private LocalDateTime createdAt;

  /** the time info about this device is last updated */
  private LocalDateTime updatedAt;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public Integer getPinCode() {
    return pinCode;
  }

  public void setPinCode(Integer pinCode) {
    this.pinCode = pinCode;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
