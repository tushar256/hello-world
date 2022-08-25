package com.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

  /**
   * Object mapper bean to perform serialization and de-serialization.
   * @return
   */
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return mapper;
  }

  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate(new BufferingClientHttpRequestFactory(
        new SimpleClientHttpRequestFactory()));
  }


}
