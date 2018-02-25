package com.sevenloldev.spring.userdevice.device.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sevenloldev.spring.userdevice.util.validation.Optional;
import com.sevenloldev.spring.userdevice.util.validation.Required;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.validator.constraints.Length;

/**
 * Entity for the category of devices
 */
@Entity
@Table(name = "DeviceType")
@JsonInclude(Include.NON_NULL)
public class DeviceType implements Serializable {
  /** unique device type */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer type;

  /** type name */
  @NotNull(groups = Required.class)
  @Length(min = 1, max = 50, groups = { Optional.class, Required.class })
  @Column
  private String name;

  /** type description */
  @Length(max = 150, groups = { Optional.class, Required.class })
  @Column
  private String description;

  /** device modelname */
  @NotNull(groups = Required.class)
  @Length(min = 1, max = 100, groups = { Optional.class, Required.class })
  @Column
  private String modelname;

  /** manufacturer name */
  @NotNull(groups = Required.class)
  @Length(min = 1, max = 100, groups = { Optional.class, Required.class })
  @Column
  private String manufacturer;

  /** getters and setters */

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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (type == null || obj == null || !(obj instanceof DeviceType)) {
      return false;
    }

    DeviceType that = (DeviceType) obj;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    // FIXME modify this implementation
    return type == null ? 0 : type.hashCode();
  }

  @Override
  public String toString() {
    return "DeviceType{" +
        "type=" + type +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", modelname='" + modelname + '\'' +
        ", manufacturer='" + manufacturer + '\'' +
        '}';
  }
}
