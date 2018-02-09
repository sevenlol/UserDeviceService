package com.sevenloldev.spring.userdevice.device.type;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * Entity for the category of devices
 */
public class DeviceType {
  /** unique device type */
  private String type;

  /** type name */
  @NotNull
  @Length(min = 1, max = 50)
  private String name;

  /** type description */
  @Length(max = 150)
  private String description;

  /** device modelname */
  @NotNull
  @Length(min = 1, max = 100)
  private String modelname;

  /** manufacturer name */
  @NotNull
  @Length(min = 1, max = 100)
  private String manufacturer;

  /** getters and setters */

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
}
