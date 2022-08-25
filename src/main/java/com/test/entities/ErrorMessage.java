package com.test.entities;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {

  private int statusCode;
  private String message;
  private String description;
  private LocalDateTime timestamp;
  private String traceId;

}
