package com.sevenloldev.spring.userdevice.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenloldev.spring.userdevice.device.type.DeviceType;
import com.sevenloldev.spring.userdevice.util.validation.Optional;
import com.sevenloldev.spring.userdevice.util.validation.Required;
import java.time.LocalDateTime;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import static com.google.common.base.Preconditions.*;

/**
 * Entity describing a device
 */
@Entity
@Table(name = "Device")
@JsonInclude(Include.NON_NULL)
public class Device {

  public Device() {}

  /**
   * construct a clone of the input device
   * @param device target device object to be cloned, must not be {@literal null}
   */
  public Device(Device device) {
    checkNotNull(device);
    this.id = device.getId();
    this.mac = device.getMac();
    this.type = device.getType();
    if (type == null && device.getDeviceType() != null) {
      type = device.getDeviceType().getType();
    }
    this.pinCode = device.getPinCode();
    this.createdAt = device.getCreatedAt();
    this.updatedAt = device.getUpdatedAt();
  }

  /** unique device ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /** device type */
  @NotNull(groups = { Required.class })
  @Transient
  private Integer type;

  /** device name */
  @NotNull(groups = { Required.class })
  @Length(min = 1, max = 50, groups = { Required.class, Optional.class })
  @Column
  private String name;

  /** mac address */
  @NotNull(groups = { Required.class })
  @Pattern(regexp = "^[a-fA-F0-9]{12}$", groups = { Required.class, Optional.class })
  @Column(unique = true)
  private String mac;

  /** pin code */
  @NotNull(groups = Required.class)
  @Min(value = 0, groups = { Required.class, Optional.class })
  @Max(value = 9999, groups = { Required.class, Optional.class })
  @Column
  private Integer pinCode;

  /** the time this device is created */
  @Column
  private LocalDateTime createdAt;

  /** the time info about this device is last updated */
  @Column
  private LocalDateTime updatedAt;

  /** embedded device type */
  @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "type")
  @Access(AccessType.PROPERTY)
  @JsonIgnore
  private DeviceType deviceType;

  @JsonIgnore
  public Integer getId() {
    return id;
  }

  /* generate string device id for response serialization */
  @JsonProperty("id")
  public String getDeviceId() {
    return id == null ? "" : id.toString();
  }

  @JsonIgnore
  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  @JsonProperty("pin_code")
  public Integer getPinCode() {
    return pinCode;
  }

  public void setPinCode(Integer pinCode) {
    this.pinCode = pinCode;
  }

  @JsonProperty("created_at")
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @JsonProperty("updated_at")
  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public DeviceType getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(DeviceType deviceType) {
    this.deviceType = deviceType;
  }
}
