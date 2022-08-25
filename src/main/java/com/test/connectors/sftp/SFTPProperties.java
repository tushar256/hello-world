package com.test.connectors.sftp;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "connector.enable.sftp", havingValue = "true")
@Configuration
@ConfigurationProperties(prefix = "sftp")
@Data
public class SFTPProperties {

  private String host;
  private int port;
  private String username;
  private String password;
  private String sftpfilepath;
  private String downloaddir;
  private String knowHostPublicKey;

}
