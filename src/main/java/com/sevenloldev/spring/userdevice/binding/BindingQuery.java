package com.sevenloldev.spring.userdevice.binding;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Request object for querying {@link Binding} entities
 */
public class BindingQuery {
  private static final String DEFAULT_SORT = "-boundAt";
  private static final String ENTITY_DEVICE = "device";

  /** pagination parameters, limit >= 1, offset >= 0 */
  @NotNull
  @Min(value = 0)
  private Integer offset;
  @NotNull
  @Min(value = 1)
  private Integer limit;

  /* filtering */

  private Integer userId;
  private Integer deviceId;

  /* embedded entities */

  @Pattern(regexp = "^device$")
  private String entities;

  /* sorting */

  @Pattern(regexp = "^-?(boundAt)$")
  private String sort = DEFAULT_SORT;

  /* getters and setters */

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    try {
      this.userId = Integer.parseInt(userId);
    } catch (Exception e) {
      // ignore
    }
  }

  public Integer getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    try {
      this.deviceId = Integer.parseInt(deviceId);
    } catch (Exception e) {
      // ignore
    }
  }

  public String getEntities() {
    return entities;
  }

  public void setEntities(String entities) {
    this.entities = entities;
  }

  public boolean attachDevices() {
    return entities != null && entities.contains(ENTITY_DEVICE);
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }
}
