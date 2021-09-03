package name.mrkandreev.ckpipeline.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import name.mrkandreev.ckpipeline.client.exception.InitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

@Configuration
public class ClickhouseExecutableBinaryConfig {
  @Value("classpath:clickhouse")
  private Resource clickhouseResource;

  @Bean
  public Path clickhouseExecutablePath() throws IOException {
    Path clickhouseUnpacked = Files.createTempFile("clickhouse", ".bin");

    try (InputStream inputStream = clickhouseResource.getInputStream();
        OutputStream outputStream = Files.newOutputStream(clickhouseUnpacked)) {
      FileCopyUtils.copy(inputStream, outputStream);
    }
    if (!clickhouseUnpacked.toFile().setExecutable(true)) {
      throw new InitializationException("Can not make clickhouse binary executable");
    }

    return clickhouseUnpacked;
  }
}
