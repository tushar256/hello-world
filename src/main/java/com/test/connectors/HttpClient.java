package com.test.connectors;

import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;


@Component
public class HttpClient {

  @Autowired
  private RestTemplate restTemplate;

  /**
   * Generic method to make REST HTTP calls by using RestTemplate.
   *
   * @param httpMethod   HTTP method e.g. GET, POST
   * @param url          the url to be invoked
   * @param headers      HTTP headers
   * @param body         the actual payload
   * @param responseType the type of response
   * @param <R>          the type of request body
   * @param <T>          the type of actual return
   * @return
   */
  public <R, T> T doRequest(@NotNull HttpMethod httpMethod, @NotEmpty String url, Map<String, String> headers, R body,
      Class<T> responseType) {

    HttpHeaders header = new HttpHeaders();
    if (!CollectionUtils.isEmpty(headers)) {
      headers.forEach((String name, String value) -> {
        header.add(name, value);
      });
    }

    HttpEntity<R> httpEntity = new HttpEntity<>(body, header);

    return restTemplate.exchange(url, httpMethod, httpEntity, responseType).getBody();
  }

}
