package com.sevenloldev.spring.userdevice.user;

import com.sevenloldev.spring.userdevice.util.error.ResourceExistException;
import com.sevenloldev.spring.userdevice.util.error.ResourceNotExistException;
import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import static com.google.common.base.Preconditions.*;

/**
 * 
 */
@Repository
public class JdbcUserRepository implements UserRepository {

  private static final String INSERT_SQL = "INSERT INTO " +
      "User(name, email, password, createdAt, updatedAt, enabled)" +
      "VALUES(?, ?, ?, ?, ?, ?)";
  private static final String QUERY_SQL = "SELECT * FROM User %sORDER BY ? %s LIMIT ? OFFSET ?";
  private static final String GET_BY_ID_SQL = "SELECT * FROM User WHERE id = ?";
  private static final String UPDATE_SQL = "UPDATE User SET %s WHERE id= ?";
  private static final String DELETE_SQL = "DELETE FROM User WHERE id= ?";

  @Autowired
  private JdbcTemplate template;

  @Override
  public String create(User user) {
    checkRequired(user);

    if (user.isEnabled() == null) {
      // enabled by default
      user.setEnabled(true);
    }

    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    KeyHolder holder = new GeneratedKeyHolder();

    try {
      template.update((con) -> {
        final PreparedStatement ps = con.prepareStatement(
            INSERT_SQL,
            Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPassword());
        ps.setObject(4, user.getCreatedAt());
        ps.setObject(5, user.getUpdatedAt());
        ps.setBoolean(6, user.isEnabled());
        return ps;
      }, holder);
    } catch (DuplicateKeyException e) {
      throw new ResourceExistException(e.getMessage(), e);
    } catch (Exception e) {
      throw new ServerErrorException(e);
    }

    if (holder.getKey() == null) {
      // failed to retrieve generated primary key
      throw new ServerErrorException();
    }
    return String.valueOf(holder.getKey().longValue());
  }

  @Override
  public UserQueryResult query(UserQuery query) {
    String queryStr = getQuerySql(query);

    List<Object> args = new ArrayList<>();
    for (Map.Entry<String, Object> entry : query.getKvs().entrySet()) {
      args.add(entry.getValue());
    }
    args.add(query.getSortField());
    args.add(query.getLimit());
    args.add(query.getOffset());
    Object[] argArr = new Object[args.size()];
    args.toArray(argArr);

    try {
      List<User> users = template.query(queryStr, argArr, new UserRowMapper());
      return new UserQueryResult(users.size(), users);
    } catch (EmptyResultDataAccessException e) {
      return new UserQueryResult(0, new ArrayList<>());
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServerErrorException(e);
    }
  }

  @Override
  public User get(String id) {
    long userId = getUserId(id);

    try {
      return template.queryForObject(GET_BY_ID_SQL,
          new Object[] { userId }, new UserRowMapper());
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotExistException(e);
    } catch (Exception e) {
      throw new ServerErrorException(e);
    }
  }

  @Override
  public void update(String id, User user) {
    long userId = getUserId(id);
    checkOptional(user);

    int rows;
    try {
     rows = template.update((con) -> {
        final PreparedStatement ps = con.prepareStatement(getUpdateSql(user));
        int idx = 1;
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
      throw new ResourceExistException(e.getMessage(), e);
    } catch (Exception e) {
      throw new ServerErrorException(e);
    }

    if (rows == 0) {
      // no such user
      throw new ResourceNotExistException();
    }
    checkArgument(rows <= 1);
  }

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
      throw new ServerErrorException(e);
    }

    if (rows == 0) {
      // no such user
      throw new ResourceNotExistException();
    }
    checkArgument(rows <= 1);
  }

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

  private void checkOptional(User user) {
    checkNotNull(user);
    checkArgument(user.getEmail() == null || !user.getEmail().isEmpty());
    checkArgument(user.getName() == null || !user.getName().isEmpty());
    checkArgument(user.getPassword() == null || !user.getPassword().isEmpty());
  }

  private void checkRequired(User user) {
    checkNotNull(user);
    checkNotNull(user.getName());
    checkNotNull(user.getEmail());
    checkNotNull(user.getPassword());
  }

  private void check(UserQuery query) {
    checkNotNull(query);
    checkNotNull(query.getOffset());
    checkNotNull(query.getLimit());
    checkNotNull(query.getKvs());
    checkNotNull(query.getSortField());
  }

  private void checkId(String id) {
    checkNotNull(id);
    checkArgument(!id.isEmpty());
    try {
      Long.parseLong(id);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException();
    }
  }

  private long getUserId(String id) {
    checkId(id);
    return Long.parseLong(id);
  }

  private String getQuerySql(UserQuery query) {
    check(query);
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
    return String.format(QUERY_SQL, sb.toString(), query.isAsc() ? "ASC" : "DESC");
  }

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

  private void appendKey(StringBuilder sb, String key) {
    String format = "%s = ?";
    if (sb.length() > 0) {
      sb.append(", ");
    }
    sb.append(String.format(format, key));
  }
}