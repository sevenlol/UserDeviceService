package com.sevenloldev.spring.userdevice.util.request;

import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Class for generating random HTTP request ID
 */
@Component
public class RequestIdGenerator {
  public String newRequestId() {
    // TODO consider other implementation
    return UUID.randomUUID().toString();
  }
}
