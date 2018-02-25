package com.sevenloldev.spring.userdevice.device.type;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

/**
 * Request object for querying {@link DeviceType} entities
 */
public class DeviceTypeQuery {
  private static final String DEFAULT_SORT = "-type";
  /** pagination parameters, limit >= 1, offset >= 0 */
  @NotNull
  @Min(value = 0)
  private Integer offset;
  @NotNull
  @Min(value = 1)
  private Integer limit;

  /* filtering */

  @Length(max = 50)
  private String name;
  @Length(max = 100)
  private String modelname;
  @Length(max = 100)
  private String manufacturer;

  /* sorting string, if starts with "-" => descending, otherwise ascending */
  @Pattern(regexp = "^-?(type|name|modelname|manufacturer)$")
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getModelname() {
    return modelname;
  }

  public void setModelname(String modelname) {
    this.modelname = modelname;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  @Override
  public String toString() {
    return "DeviceTypeQuery{" +
        "offset=" + offset +
        ", limit=" + limit +
        ", name='" + name + '\'' +
        ", modelname='" + modelname + '\'' +
        ", manufacturer='" + manufacturer + '\'' +
        ", sort='" + sort + '\'' +
        '}';
  }
}
