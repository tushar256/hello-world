package com.test.entities;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Audit {

  @Id
  private String id;
  private String applicationName;
  private String objectId;
  private String api;
  private String requestBody;
  private String responseBody;
  private LocalDateTime requestDateTime;
  private Long apiResponseTime;
  private String traceId;

}
