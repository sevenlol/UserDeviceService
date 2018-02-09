package com.sevenloldev.spring.userdevice.user;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

public class UserQuery {
  private static final String DEFAULT_SORT_FIELD = "updatedAt";
  private static final boolean DEFAULT_SORT_ORDER = false;
  private Integer limit;
  private Integer offset;
  private Map<String, Object> kvs = new HashMap<>();
  private String sortField = DEFAULT_SORT_FIELD;
  private boolean asc = DEFAULT_SORT_ORDER;

  public UserQuery(User user) {
    checkNotNull(user);
    if (user.getEmail() != null) {
      kvs.put("email", user.getEmail());
    }
    if (user.getName() != null) {
      kvs.put("name", user.getName());
    }
    if (user.isEnabled() != null) {
      kvs.put("enabled", user.isEnabled());
    }
  }

  public UserQuery(UserQueryRequest req) {
    checkNotNull(req);
    if (req.getEmail() != null) {
      checkArgument(!req.getEmail().isEmpty());
      kvs.put("email", req.getEmail());
    }
    if (req.getName() != null) {
      checkArgument(!req.getName().isEmpty());
      kvs.put("name", req.getName());
    }
    if (req.getEnabled() != null) {
      kvs.put("enabled", req.getEnabled());
    }
    limit = req.getLimit();
    offset = req.getOffset();
    if (req.getSortedBy() != null) {
      String sortedBy = req.getSortedBy();
      if (sortedBy.startsWith("-")) {
        asc = false;
        sortedBy = sortedBy.substring(1);
      }
      sortField = sortedBy;
    }
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public Map<String, Object> getKvs() {
    return kvs;
  }

  public String getSortField() {
    return sortField;
  }

  public boolean isAsc() {
    return asc;
  }
}
