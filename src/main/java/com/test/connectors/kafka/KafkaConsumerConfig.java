package com.test.connectors.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@ConditionalOnProperty(name = "connector.enable.kafka_consumer", havingValue = "true")
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

  @Value("${kafka.bootstrapservers}")
  private String bootstrapAddress;

  @Value("${ssl.truststore_location}")
  private String sslTruststoreLocation;

  @Value("${ssl.truststore_password}")
  private String sslTruststorePassword;

  @Value("${ssl.keystore_location}")
  private String sslKeystoreLocation;

  @Value("${ssl.keystore_password}")
  private String sslKeystorePassword;

  @Value("${ssl.key_password}")
  private String sslKeyPassword;

  @Value("${kafka.consumer.groupId}")
  private String groupId;

  /**
   * Configure Kafka Consumer properties.
   * @return
   */
  @Bean
  public ConsumerFactory<String, String> consumerFactory() {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
    props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocation);
    props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePassword);

    props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslKeystoreLocation);
    props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslKeystorePassword);
    props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, sslKeyPassword);

    //automatically reset the offset to the latest offset
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    //The maximum number of records returned in a single call to poll()
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
    //If poll() is not called before expiration of this timeout, then the consumer is considered failed
    // and the group will rebalance in order to reassign the partitions to another member.
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "9000000");
    //controls the maximum amount of time the client will wait for the response of a request.
    // If the response is not received before the timeout elapses the client will resend the request
    // if necessary or fail the request if retries are exhausted
    props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "600000");
    //The timeout used to detect client failures when using Kafka's group management facility.
    // The client sends periodic heartbeats to indicate its liveness to the broker.
    // If no heartbeats are received by the broker before the expiration of this session timeout,
    // then the broker will remove this client from the group and initiate a rebalance.
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");

    return new DefaultKafkaConsumerFactory<>(props);

  }

  /**
   * Configure the Kafka listener Container factory.
   * @return
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory( ConsumerFactory consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(1);
    return factory;

  }
}
