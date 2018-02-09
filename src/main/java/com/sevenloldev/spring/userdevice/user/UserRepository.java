package com.sevenloldev.spring.userdevice.user;

public interface UserRepository {
  String create(User user);
  UserQueryResult query(UserQuery query);
  User get(String id);
  void update(String id, User user);
  void delete(String id);
}
