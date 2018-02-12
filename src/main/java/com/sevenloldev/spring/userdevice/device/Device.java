package com.sevenloldev.spring.userdevice.device;

import com.sevenloldev.spring.userdevice.device.type.DeviceType;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.Cascade;

/**
 * Entity describing a device
 */
@Entity
@Table(name = "Device")
public class Device {
  /** unique device ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /** device type */
  @NotEmpty
  @ManyToOne(cascade = CascadeType.ALL, targetEntity = DeviceType.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "type")
  private Integer type;

  /** mac address */
  @NotNull
  @Pattern(regexp = "^[a-fA-F0-9]{12}$")
  @Column
  private String mac;

  /** pin code */
  @NotNull
  @Min(0)
  @Max(9999)
  @Column
  private Integer pinCode;

  /** the time this device is created */
  @Column
  private LocalDateTime createdAt;

  /** the time info about this device is last updated */
  @Column
  private LocalDateTime updatedAt;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
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