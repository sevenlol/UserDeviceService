package com.sevenloldev.spring.userdevice.user;

import com.sevenloldev.spring.userdevice.util.validation.Optional;
import com.sevenloldev.spring.userdevice.util.validation.Required;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Entity describing a user
 */
public class User {
  private static final boolean DEFAULT_ENABLED = false;

  /** unique user ID */
  private String id;

  /** username */
  @NotNull(groups = { Required.class })
  @Length(min = 1, groups = { Required.class, Optional.class})
  private String name;

  /** user email */
  @NotNull(groups = { Required.class })
  @Email(groups = { Required.class, Optional.class })
  private String email;

  /** user password */
  @NotNull(groups = { Required.class })
  @Length(min = 6, groups = { Required.class, Optional.class })
  private String password;

  /** when is this user created */
  private LocalDateTime createdAt;

  /** time of last update on this user entity */
  private LocalDateTime updatedAt;

  /** flag to enable/disable this user */
  private Boolean enabled = DEFAULT_ENABLED;

  // getter and setter

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public Boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

