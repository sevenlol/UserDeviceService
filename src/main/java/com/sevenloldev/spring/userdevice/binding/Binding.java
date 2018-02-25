package com.sevenloldev.spring.userdevice.binding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sevenloldev.spring.userdevice.device.Device;
import com.sevenloldev.spring.userdevice.user.User;
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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Entity describing a binding between {@link User} and {@link Device}
 */
@Entity
@Table(name = "Binding")
@NamedEntityGraph(
    name = "Binding.device",
    attributeNodes = @NamedAttributeNode("device"))
@JsonInclude(Include.NON_NULL)
public class Binding {
  /** unique binding id */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /** embedded device object */
  @NotNull(groups = Required.class)
  @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
  @JoinColumn(name = "deviceId", referencedColumnName = "id")
  @Access(AccessType.PROPERTY)
  private Device device;

  /** separate device ID attribute */
  @Transient
  private Integer deviceId;

  /** user ID attribute */
  @NotNull(groups = Required.class)
  @Column
  private Integer userId;

  /** binding time */
  @Column
  private LocalDateTime boundAt;

  /* getters and setters */

  @JsonIgnore
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public String getBindingId() {
    return id == null ? null : id.toString();
  }

  @JsonIgnore
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("device")
  public Device getDevice() {
    return device;
  }

  @JsonIgnore
  public void setDevice(Device device) {
    this.device = device;
    if (device != null) {
      this.deviceId = device.getId();
    }
  }

  @JsonProperty("device_id")
  public String getDeviceId() {
    return deviceId == null ? null : deviceId.toString();
  }

  @JsonProperty("device_id")
  @Transient
  public void setDeviceId(String id) {
    if (id == null) {
      return;
    }

    try {
      int deviceId = Integer.parseInt(id);
      if (device == null) {
        device = new Device();
      }
      device.setId(deviceId);
    } catch (NumberFormatException e) {
      // do nothing
    }
  }

  @JsonIgnore
  public void setDeviceId(Integer deviceId) {
    this.deviceId = deviceId;
  }

  @JsonProperty("user_id")
  public String getUserId() {
    return userId == null ? null : userId.toString();
  }

  @JsonProperty("user_id")
  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public LocalDateTime getBoundAt() {
    return boundAt;
  }

  public void setBoundAt(LocalDateTime boundAt) {
    this.boundAt = boundAt;
  }

  @Override
  public String toString() {
    return "Binding{" +
        "id=" + id +
        ", device=" + device +
        ", deviceId=" + deviceId +
        ", userId=" + userId +
        ", boundAt=" + boundAt +
        '}';
  }
}
