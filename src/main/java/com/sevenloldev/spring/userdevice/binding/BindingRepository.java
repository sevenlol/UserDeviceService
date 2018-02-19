package com.sevenloldev.spring.userdevice.binding;

import com.sevenloldev.spring.userdevice.util.response.QueryResponse;

/**
 * Interface for managing {@link Binding} CRUD operations
 */
public interface BindingRepository {
  String create(Binding binding);
  QueryResponse<Binding> query(BindingQuery query);
  Binding get(String id);
  void update(String id, Binding binding);
  void delete(String id);
}
