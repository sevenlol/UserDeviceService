package com.sevenloldev.spring.userdevice.util.error;

public class ResourceExistException extends RuntimeException {
  public ResourceExistException() {
  }

  public ResourceExistException(String message) {
    super(message);
  }

  public ResourceExistException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceExistException(Throwable cause) {
    super(cause);
  }

  public ResourceExistException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
