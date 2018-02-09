package com.sevenloldev.spring.userdevice.user;

import java.util.List;

import static com.google.common.base.Preconditions.*;

public class UserQueryResult {
  private int total;
  private List<User> users;

  public UserQueryResult(int total,
      List<User> users) {
    checkNotNull(users);
    checkArgument(total >= 0);
    this.total = total;
    this.users = users;
  }

  public int getTotal() {
    return total;
  }

  public List<User> getUsers() {
    return users;
  }
}
