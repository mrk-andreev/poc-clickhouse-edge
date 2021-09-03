package name.mrkandreev.ckpipeline.server.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketSessionConfig {
  @Bean
  public Map<String, Session> wsSessions() {
    return new ConcurrentHashMap<>();
  }
}
