package com.test.connectors.kafka;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@ConditionalOnProperty(name = "connector.enable.kafka_producer", havingValue = "true")
@Configuration
public class KafkaProducerConfig {

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

  /**
   * Configure Kafka Producer properties.
   * @return
   */
  @Bean
  public ProducerFactory<String, String> producerFactory() {

    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    configProps.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
    configProps.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, "PKCS12");
    configProps.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslKeystoreLocation);
    configProps.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslKeystorePassword);

    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);

    configProps.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, "PKCS12");
    configProps.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocation);
    configProps.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePassword);

    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }


}
