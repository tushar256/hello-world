package com.test.config;

import com.test.entities.ErrorMessage;
import com.test.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @Autowired
  private Tracer tracer;

  /**
   * Global exception handler for catching exception of type ResourceNotFoundException.
   * @param ex exception of type ResourceNotFoundException
   * @param request web request
   * @return
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorMessage> resourceNotFoundException(ResourceNotFoundException ex,
      WebRequest request) {
    ErrorMessage message = new ErrorMessage(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        request.getDescription(false),
        LocalDateTime.now(),
        Optional.ofNullable(tracer.currentSpan().context().traceId()).orElse(""));

    log.error("Error occurred while calling {} {}", request, message);
    return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
  }

  /**
   * Global exception handler for catching exception of type Exception.
   * @param ex exception of type Exception
   * @param request web request
   * @return
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        ex.getMessage(),
        request.getDescription(false),
        LocalDateTime.now(),
        Optional.ofNullable(tracer.currentSpan().context().traceId()).orElse(""));

    log.error("Error occurred while calling {} {}", request, message);
    return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}