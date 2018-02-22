package com.sevenloldev.spring.userdevice.util.error;

/**
 * Exception indicating resource targeted by the operation cannot be found
 */
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
