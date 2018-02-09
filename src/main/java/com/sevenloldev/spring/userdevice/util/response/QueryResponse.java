package com.sevenloldev.spring.userdevice.util.response;

import java.util.List;

import static com.google.common.base.Preconditions.*;

/**
 * Response wrapper for queries
 * @param <T> query result type
 */
public class QueryResponse <T> {
  /** total entities that match the current query */
  private final int total;
  /** current batch */
  private final List<T> results;

  public QueryResponse(int total, List<T> results) {
    checkNotNull(results);
    checkArgument(total >= 0);
    this.total = total;
    this.results = results;
  }

  public int getTotal() {
    return total;
  }

  public List<T> getResults() {
    return results;
  }
}
