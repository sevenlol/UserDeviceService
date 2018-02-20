package com.sevenloldev.spring.userdevice.device;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Request object for querying {@link Device} entities
 */
public class DeviceQuery {
  private static final String DEFAULT_SORT = "-updatedAt";
  /** pagination parameters, limit >= 1, offset >= 0 */
  @NotNull
  @Min(value = 0)
  private Integer offset;
  @NotNull
  @Min(value = 1)
  private Integer limit;

  /* filtering */

  private Integer type;
  private String mac;
  private String name;

  /* sorting string, if starts with "-" => descending, otherwise ascending */
  @Pattern(regexp = "^-?(type|name|mac|createdAt|updatedAt)$")
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }
}
