package com.test.connectors.kafka;

import com.test.audit.Auditable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@ConditionalOnProperty(name = "connector.enable.kafka_producer", havingValue = "true")
@Slf4j
@Service
public class KafkaSender {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private Tracer tracer;

  @Value("${kafka.producer.topic}")
  private String topic;

  /**
   * This method sends a message to Kafka broker.
   *
   * @param data The payload to send
   * @param key  The message key
   */
  @Auditable
  public void sendMessage(String data, String key) {

    Message<String> message = MessageBuilder
        .withPayload(data)
        .setHeader(KafkaHeaders.TOPIC, topic)
        .setHeader(KafkaHeaders.MESSAGE_KEY, key)
        .setHeader("X-TraceId", tracer.currentSpan().context().traceId())
        .build();

    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);

    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

      @Override
      public void onSuccess(SendResult<String, String> result) {
        log.info(
            "Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset()
                + "]");
      }

      @Override
      public void onFailure(Throwable ex) {
        log.info("Unable to send message=[" + message + "] due to : " + ex.getMessage());
      }
    });
  }


}
