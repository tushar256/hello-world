package com.test.connectors.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.ByteArrayInputStream;
import javax.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "connector.enable.sftp", havingValue = "true")
@Slf4j
@Service
public class SFTP {

  @Autowired
  private SFTPProperties sftpProperties;

  /**
   *
   * @return
   * @throws JSchException
   */
  public ChannelSftp getChannelSftp() throws JSchException {
    ChannelSftp sftp;

    JSch ssh = new JSch();
    String knownHostPublicKey = sftpProperties.getKnowHostPublicKey();
    ssh.setKnownHosts(new ByteArrayInputStream(knownHostPublicKey.getBytes()));

    Session session = ssh.getSession(sftpProperties.getUsername(), sftpProperties.getHost(),
        sftpProperties.getPort());

    session.setPassword(sftpProperties.getPassword());
    session.connect();
    log.debug("getChannelSftp()--connecting SFTP location using username " + sftpProperties
        .getUsername());

    sftp = (ChannelSftp) session.openChannel("sftp");
    return sftp;
  }


  public void downloadFile(@NotEmpty String fileName) {

    ChannelSftp channelSftp = null;
    try {
      channelSftp = getChannelSftp();
      channelSftp.connect();

      String downloadDir = sftpProperties.getDownloaddir();
      String sftpFilePath = sftpProperties.getSftpfilepath();

      channelSftp.get( sftpFilePath + fileName, downloadDir + fileName);

    } catch (Exception ex) {
      log.error("downloadFile()--error downloading from SFTP location", ex);

    } finally {
      if(channelSftp!= null){
        channelSftp.exit();
      }
    }

  }

  public void uploadFile(@NotEmpty String localFilePath, @NotEmpty String remoteFilePath) {

    ChannelSftp channelSftp = null;

    try{
      channelSftp = getChannelSftp();
      channelSftp.connect();
      channelSftp.put(localFilePath, remoteFilePath);

    } catch (Exception ex){
      log.error("uploadFile() -- error uploading to SFTP location", ex);

    } finally {
      if(channelSftp!= null){
        channelSftp.exit();
      }
    }

  }

}
