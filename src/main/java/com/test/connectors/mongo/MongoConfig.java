package com.test.connectors.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;


@ConditionalOnProperty(name = "connector.enable.mongo", havingValue = "true")
@Configuration
@ImportAutoConfiguration({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class MongoConfig {

  @Value("${ssl.keystore_location}")
  private String keyStorePath;

  @Value("${ssl.keystore_password}")
  private String keyStorePassword;

  @Value("${mongo.host}")
  private String host;

  @Value("${mongo.port}")
  private String port;

  @Value("${mongo.user}")
  private String user;

  @Value("${mongo.password}")
  private String password;

  @Value("${mongo.database}")
  private String database;


  /**
   * Configuration for connection to Mongo Database.
   *
   * @return returns MongoClient
   * @throws Exception this method throws Exception
   */

  @Bean
  public MongoClient mongoClient() throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    InputStream is = new FileInputStream(keyStorePath);
    keyStore.load(is, keyStorePassword.toCharArray());

    SSLContext sslContext = SSLContextBuilder
        .create()
        .loadKeyMaterial(keyStore, keyStorePassword.toCharArray())
        .loadTrustMaterial((chain, authType) -> true)
        .build();

    MongoCredential credential = MongoCredential
        .createCredential(user, database, password.toCharArray());

    MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
        .credential(credential)
        .applyToClusterSettings(builder ->
            builder.hosts(Arrays.asList(new ServerAddress(host, Integer.parseInt(port)))))
        .applyToSslSettings(builder ->
            builder.context(sslContext).enabled(true))
        .build();

    return MongoClients.create(mongoClientSettings);
  }


  @Bean
  public MongoTemplate mongoTemplate(MongoClient mongoClient) throws Exception {
    return new MongoTemplate(mongoClient, database);
  }
}
