package name.mrkandreev.ckpipeline.server.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobServiceConfig {
  @Bean
  public Map<String, JobDto> jobs() {
    return new ConcurrentHashMap<>();
  }
}
