package com.sevenloldev.spring.userdevice.util.health;

import com.sevenloldev.spring.userdevice.util.error.ServerErrorException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthIndicatorController {
  private static final String READINESS_SQL = "SELECT 1 from User";
  @Autowired
  /** Injected jdbc template */
  private JdbcTemplate template;

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/healthy")
  public String checkHealthy() {
    // only check if this endpoint is working
    return LocalDateTime.now().toString();
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/ready")
  public String checkReady() {
    try {
      // execute a simple query
      template.execute(READINESS_SQL);
    } catch (Exception e) {
      throw new ServerErrorException(e);
    }
    return "ready";
  }
}
