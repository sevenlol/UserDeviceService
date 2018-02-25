package com.sevenloldev.spring.userdevice.user;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

/**
 * Query object for {@link User} entities.
 */
public class UserQuery {
  private static final String DEFAULT_SORT_FIELD = "updatedAt";
  private static final boolean DEFAULT_SORT_ORDER = false;
  /** pagination parameter (limit and offset) */
  private Integer limit;
  private Integer offset;
  /** key-value pairs for filtering {@link User} entities */
  private Map<String, Object> kvs = new HashMap<>();
  /** sorting field and order */
  private String sort = DEFAULT_SORT_FIELD;
  private boolean asc = DEFAULT_SORT_ORDER;

  /**
   * Use a {@link User} object as filtering condition
   * @param user name, email, enabled fields will be used as filtering value (if set)
   */
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

  /**
   * Generate {@link UserQuery} with given {@link UserQueryRequest}
   */
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
    if (req.getSort() != null) {
      String sortedBy = req.getSort();
      if (sortedBy.startsWith("-")) {
        asc = false;
        sortedBy = sortedBy.substring(1);
      } else {
        asc = true;
      }
      sort = sortedBy;
    }
  }

  // getters and setters

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

  public String getSort() {
    return sort;
  }

  public boolean isAsc() {
    return asc;
  }

  @Override
  public String toString() {
    return "UserQuery{" +
        "limit=" + limit +
        ", offset=" + offset +
        ", kvs=" + kvs +
        ", sort='" + sort + '\'' +
        ", asc=" + asc +
        '}';
  }
}
