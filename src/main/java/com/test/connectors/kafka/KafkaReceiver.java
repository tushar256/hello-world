package com.test.connectors.kafka;

import com.test.audit.Auditable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;


@ConditionalOnProperty(name = "connector.enable.kafka_consumer", havingValue = "true")
@Slf4j
@Service
public class KafkaReceiver {


  /**
   * This method listens for messages from Kafka Broker on a topic.
   * @param message message received
   * @param traceId optional traceId header
   */
  @Auditable
  @KafkaListener(topics = "#{'${kafka.consumer.topic}'}", containerFactory = "kafkaListenerContainerFactory")
  public void listenMessage(@Payload String message,  @Header("X-TraceId") String traceId) {

    log.info("Received Kafka message: " + message);

  }

}
