package com.sevenloldev.spring.userdevice.user;

import com.sevenloldev.spring.userdevice.util.error.ResourceExistException;
import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import com.sevenloldev.spring.userdevice.util.response.QueryResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.*;

/**
 * Managing CRUD operations for {@link User} using jdbc interfaces
 */
@Repository
public class JdbcUserRepository implements UserRepository {
  private final Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);

  private static final String INSERT_SQL = "INSERT INTO " +
      "User(name, email, password, createdAt, updatedAt, enabled)" +
      "VALUES(?, ?, ?, ?, ?, ?)";
  private static final String QUERY_SQL = "SELECT * FROM User %s" +
      "ORDER BY %s %s LIMIT ? OFFSET ?";
  private static final String COUNT_SQL = "SELECT count(*) FROM User %s";
  private static final String GET_BY_ID_SQL = "SELECT * FROM User WHERE id = ?";
  private static final String UPDATE_SQL = "UPDATE User SET %s WHERE id= ?";
  private static final String DELETE_SQL = "DELETE FROM User WHERE id= ?";

  @Autowired
  /** Injected jdbc template */
  private JdbcTemplate template;

  @Override
  public String create(User user) {
    checkRequired(user);

    if (user.isEnabled() == null) {
      // enabled by default
      user.setEnabled(User.DEFAULT_ENABLED);
    }

    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    logger.debug("Create user object = {}", user);

    // holder for retrieving the auto generated primary key
    KeyHolder holder = new GeneratedKeyHolder();

    try {
      template.update((con) -> {
        final PreparedStatement ps = con.prepareStatement(
            INSERT_SQL,
            Statement.RETURN_GENERATED_KEYS);
        // bind parameters
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setObject(4, user.getCreatedAt());
        ps.setObject(5, user.getUpdatedAt());
        ps.setBoolean(6, user.isEnabled());
        return ps;
      }, holder);
    } catch (DuplicateKeyException e) {
      // user with the specified email/name already exists
      logger.error("Email={} or username={} already exists, error={}",
          user.getEmail(), user.getName(), e.getMessage());
      logger.debug("Error=", e);
      throw new ResourceExistException(e.getMessage(), e);
    } catch (Exception e) {
      // operation failed
      logger.error("Create user failed, error={}", e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }

    if (holder.getKey() == null) {
      // failed to retrieve generated primary key
      logger.error("Failed to retrieve generated user ID");
      throw new ServerErrorException();
    }
    logger.debug("User created successfully, ID={}, user={}", holder.getKey().longValue(), user);
    return String.valueOf(holder.getKey().longValue());
  }

  @Transactional
  @Override
  public QueryResponse<User> query(UserQuery query) {
    // generate query string
    String queryStr = getQuerySql(query);
    // generate SQL string for counting the total rows that matches the query
    String countStr = getCountSql(query);

    logger.debug("Query={}, query sql={}, count sql={}", query, queryStr, countStr);

    // arguments for the query operation
    List<Object> args = new ArrayList<>();
    for (Map.Entry<String, Object> entry : query.getKvs().entrySet()) {
      args.add(entry.getValue());
    }
    args.add(query.getLimit());
    args.add(query.getOffset());
    Object[] argArr = new Object[args.size()];
    args.toArray(argArr);
    // arguments for the count operation (minus limit & offset)
    Object[] countArgArr = new Object[args.size() - 2];
    System.arraycopy(argArr, 0, countArgArr, 0, args.size() - 2);

    try {
      List<User> users = template.query(queryStr, argArr, new UserRowMapper());
      Integer count = template.queryForObject(countStr, countArgArr, Integer.class);

      logger.debug("Users={}, total={}", users, count);
      return new QueryResponse<>(count == null ? 0 : count, users);
    } catch (EmptyResultDataAccessException e) {
      // no matching rows
      logger.debug("No matching users");
      return new QueryResponse<>(0, new ArrayList<>());
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to query users, error={}", e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }
  }

  @Cacheable("users")
  @Override
  public User get(String id) {
    long userId = getUserId(id);

    logger.debug("Retrieve user by ID={}, sql={}", userId, GET_BY_ID_SQL);
    try {
      return template.queryForObject(GET_BY_ID_SQL,
          new Object[] { userId }, new UserRowMapper());
    } catch (EmptyResultDataAccessException e) {
      // user does not exist
      logger.error("User with ID={} does not exist", userId);
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to retrieve user with ID={}, error={}", userId, e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }
  }

  @CacheEvict(cacheNames = "users", key = "#id")
  @Override
  public void update(String id, User user) {
    long userId = getUserId(id);
    checkOptional(user);

    logger.debug("Update user with ID={}, state={}", userId, user);

    int rows;
    try {
     rows = template.update((con) -> {
        final PreparedStatement ps = con.prepareStatement(getUpdateSql(user));
        int idx = 1;
        // merge state
        if (user.getName() != null) {
          ps.setString(idx++, user.getName());
        }
        if (user.getEmail() != null) {
          ps.setString(idx++, user.getEmail());
        }
        if (user.getPassword() != null) {
          ps.setString(idx++, user.getPassword());
        }
        ps.setObject(idx++, LocalDateTime.now());
        ps.setLong(idx, userId);
        return ps;
      });
    } catch (DuplicateKeyException e) {
      // update email/username already exists
      logger.error("Failed to update user ID={}, duplicate email or username, error={}",
          userId, e.getMessage());
      logger.debug("Error=", e);
      throw new ResourceExistException(e.getMessage(), e);
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to update user ID={}, error={}", userId, e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }

    if (rows == 0) {
      // no such user
      logger.error("No user with ID={} to update", userId);
      throw new ResourceNotExistException();
    }
    // should only get one user
    checkArgument(rows == 1);
  }

  @CacheEvict(cacheNames = "users", key = "#id")
  @Override
  public void delete(String id) {
    long userId = getUserId(id);

    int rows;
    try {
      rows = template.update((con) -> {
        final PreparedStatement ps = con.prepareStatement(DELETE_SQL);
        ps.setLong(1, userId);
        return ps;
      });
    } catch (Exception e) {
      // operation failed
      logger.error("Failed to delete user ID={}, error={}", userId, e.getMessage());
      logger.debug("Error=", e);
      throw new ServerErrorException(e);
    }

    if (rows == 0) {
      // no such user
      logger.error("No user with ID={} to delete", userId);
      throw new ResourceNotExistException();
    }
    // should only delete one user
    checkArgument(rows == 1);
  }

  /**
   * Helper class that maps table rows to {@link User} object
   */
  private class UserRowMapper implements RowMapper<User> {
    @Nullable
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
      User user = new User();
      user.setId(String.valueOf(rs.getLong("id")));
      user.setName(rs.getString("name"));
      user.setEmail(rs.getString("email"));
      user.setPassword(rs.getString("password"));
      user.setCreatedAt(rs.getObject("createdAt", LocalDateTime.class));
      user.setUpdatedAt(rs.getObject("updatedAt", LocalDateTime.class));
      user.setEnabled(rs.getBoolean("enabled"));
      return user;
    }
  }

  /**
   * Check if the user object contains empty fields
   * @param user {@link User} to be checked
   */
  private void checkOptional(User user) {
    checkNotNull(user);
    checkArgument(user.getEmail() == null || !user.getEmail().isEmpty());
    checkArgument(user.getName() == null || !user.getName().isEmpty());
    checkArgument(user.getPassword() == null || !user.getPassword().isEmpty());
  }

  /**
   * Check if the user object is missing required fields
   */
  private void checkRequired(User user) {
    checkNotNull(user);
    checkNotNull(user.getName());
    checkNotNull(user.getEmail());
    checkNotNull(user.getPassword());
  }

  /**
   * Check if user query is valid
   */
  private void check(UserQuery query) {
    checkNotNull(query);
    checkNotNull(query.getOffset());
    checkNotNull(query.getLimit());
    checkNotNull(query.getKvs());
    checkNotNull(query.getSort());
  }

  /**
   * Check if user id is a string representing a valid number
   */
  private void checkId(String id) {
    checkNotNull(id);
    checkArgument(!id.isEmpty());
    try {
      Long.parseLong(id);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Check and parse user ID from string to long
   */
  private long getUserId(String id) {
    checkId(id);
    return Long.parseLong(id);
  }

  /**
   * Generate SQL query string with the given {@link UserQuery}
   * @param query target query
   * @return generated SQL query string (filtering,sorting and pagination)
   */
  private String getQuerySql(UserQuery query) {
    check(query);
    return String.format(
        QUERY_SQL,
        getFilterSql(query),
        query.getSort(),
        query.isAsc() ? "ASC" : "DESC");
  }

  /**
   * Generate SQL query string for counting total rows of the given {@link UserQuery}
   * @param query target query
   * @return generated SQL query string
   */
  private String getCountSql(UserQuery query) {
    check(query);
    return String.format(COUNT_SQL, getFilterSql(query));
  }

  /**
   * Generate where clause from the given {@link UserQuery}
   * @param query target query
   * @return generated partial SQL query string (where clause)
   */
  private String getFilterSql(UserQuery query) {
    StringBuilder sb = new StringBuilder();
    if (!query.getKvs().entrySet().isEmpty()) {
      sb.append("WHERE ");
    }
    int count = 0;
    for (Map.Entry<String, Object> entry : query.getKvs().entrySet()) {
      if (count > 0) {
        sb.append("AND ");
      }
      sb.append(String.format("%s = ? ", entry.getKey()));
      count++;
    }
    return sb.toString();
  }

  /**
   * Generate update SQL query (not including ID where clause)
   * @param user target {@link User}
   * @return generated SQL query string for update
   */
  private String getUpdateSql(User user) {
    checkArgument(user.getEmail() != null ||
        user.getName() != null || user.getPassword() != null);
    StringBuilder sb = new StringBuilder();
    if (user.getName() != null) {
      appendKey(sb, "name");
    }
    if (user.getEmail() != null) {
      appendKey(sb, "email");
    }
    if (user.getPassword() != null) {
      appendKey(sb, "password");
    }
    appendKey(sb, "updatedAt");
    return String.format(UPDATE_SQL, sb.toString());
  }

  /**
   * Helper method for generating update attribute-value pair in SQL query string
   */
  private void appendKey(StringBuilder sb, String key) {
    String format = "%s = ?";
    if (sb.length() > 0) {
      sb.append(", ");
    }
    sb.append(String.format(format, key));
  }
}
