package com.sevenloldev.spring.userdevice.util.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for controllers
 */
@ControllerAdvice
public class ErrorContoller extends ResponseEntityExceptionHandler {
  /** default error messages */
  private static class ErrorMsg {
    static final String BAD_REQUEST = "Invalid request";
    static final String NOT_FOUND = "Resource does not exist";
    static final String CONFLICT = "Resource with the same identity already exists";
    static final String SERVER_ERROR = "Server error";
  }

  /**
   * Exception handler for {@link IllegalArgumentException}
   * @param e target exception object
   * @param req current request
   * @return HTTP response with {@link HttpStatus} 400 indicating malformed request
   */
  @ExceptionHandler(value = { IllegalArgumentException.class })
  public ResponseEntity<Object> handleBadRequestError(RuntimeException e, WebRequest req) {
    return handleExceptionInternal(e, ErrorMsg.BAD_REQUEST,
        new HttpHeaders(), HttpStatus.BAD_REQUEST, req);
  }

  /**
   * Exception handler for {@link ResourceNotExistException}
   * @param e target exception object
   * @param req current request
   * @return HTTP response with {@link HttpStatus} 404 indicating interaction on missing resources
   */
  @ExceptionHandler(value = { ResourceNotExistException.class })
  public ResponseEntity<Object> handleNotFoundError(RuntimeException e, WebRequest req) {
    return handleExceptionInternal(e, ErrorMsg.NOT_FOUND,
        new HttpHeaders(), HttpStatus.NOT_FOUND, req);
  }

  /**
   * Exception handler for {@link ResourceExistException}
   * @param e target exception object
   * @param req current request
   * @return HTTP response with {@link HttpStatus} 409 indicating interaction conflicts
   * with existing resources
   */
  @ExceptionHandler(value = { ResourceExistException.class })
  public ResponseEntity<Object> handleConflictError(RuntimeException e, WebRequest req) {
    return handleExceptionInternal(e, ErrorMsg.CONFLICT,
        new HttpHeaders(), HttpStatus.CONFLICT, req);
  }

  /**
   * Exception handler for {@link ServerErrorException}
   * @param e target exception object
   * @param req current request
   * @return HTTP response with {@link HttpStatus} 500 indicating something went wrong on the
   * server side
   */
  @ExceptionHandler(value = { ServerErrorException.class })
  public ResponseEntity<Object> handleServerError(RuntimeException e, WebRequest req) {
    return handleExceptionInternal(e, ErrorMsg.SERVER_ERROR,
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, req);
  }
}
