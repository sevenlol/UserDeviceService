package com.sevenloldev.spring.userdevice.util.error;

public class ResourceNotExistException extends RuntimeException {
  public ResourceNotExistException() {
  }

  public ResourceNotExistException(String message) {
    super(message);
  }

  public ResourceNotExistException(String message, Throwable cause) {
    super(message, cause);
  }

  public ResourceNotExistException(Throwable cause) {
    super(cause);
  }

  public ResourceNotExistException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
