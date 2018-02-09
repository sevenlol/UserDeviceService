package com.sevenloldev.spring.userdevice.user;

import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Data transfer object for user query result
 */
public class UserQueryResult {
  /** total number of users that match the filtering condition */
  private int total;
  /** current batch of {@link User} objects */
  private List<User> users;

  public UserQueryResult(int total,
      List<User> users) {
    checkNotNull(users);
    checkArgument(total >= 0);
    this.total = total;
    this.users = users;
  }

  /** getters */

  public int getTotal() {
    return total;
  }

  public List<User> getUsers() {
    return users;
  }
}
