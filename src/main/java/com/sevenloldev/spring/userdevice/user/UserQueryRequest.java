package com.sevenloldev.spring.userdevice.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserQueryRequest {
  @NotNull
  @Min(value = 0)
  private Integer offset;
  @NotNull
  @Min(value = 1)
  private Integer limit;
  private String email;
  private String name;
  private Boolean enabled;
  @Pattern(regexp = "^-?(name|email|createdAt|updatedAt)$")
  private String sortedBy;

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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public String getSortedBy() {
    return sortedBy;
  }

  public void setSortedBy(String sortedBy) {
    this.sortedBy = sortedBy;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }
}
