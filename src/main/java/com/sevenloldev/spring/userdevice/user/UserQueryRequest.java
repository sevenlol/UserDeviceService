package com.sevenloldev.spring.userdevice.user;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Request object for query user API
 */
public class UserQueryRequest {
  /** pagination parameters, limit >= 1, offset >= 0 */
  @NotNull
  @Min(value = 0)
  private Integer offset;
  @NotNull
  @Min(value = 1)
  private Integer limit;
  /** email for filtering users */
  private String email;
  /** name for filtering users */
  private String name;
  /** enabled flag for filtering users */
  private Boolean enabled;
  /** text representing sorting field and order
   *  E.g., -email to sort email in descending order
   */
  @Pattern(regexp = "^-?(name|email|createdAt|updatedAt)$")
  private String sort;

  /** getters and setters */

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

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }
}
