package com.sevenloldev.spring.userdevice.util.request;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HTTP request filter that assign a random request ID for logging
 */
@Component
public class RequestLoggingFilter implements Filter {
  private static final String REQUEST_DATA_KEY = "requestData";
  private static final String REQUEST_DATA_FMT = "[req=%s] ";

  @Autowired
  private RequestIdGenerator generator;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // do nothing
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      MDC.put(REQUEST_DATA_KEY, getRequestData(generator.newRequestId()));
      chain.doFilter(request, response);
    } finally {
      // cleanup thread local data
      MDC.clear();
    }
  }

  @Override
  public void destroy() {
    // do nothing
  }

  private String getRequestData(String requestId) {
    return String.format(REQUEST_DATA_FMT, requestId);
  }
}
