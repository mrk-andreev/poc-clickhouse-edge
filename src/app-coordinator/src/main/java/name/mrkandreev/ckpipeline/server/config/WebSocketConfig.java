package name.mrkandreev.ckpipeline.server.config;

import lombok.extern.log4j.Log4j2;
import name.mrkandreev.ckpipeline.server.controller.WebSocketEndpoint;
import name.mrkandreev.ckpipeline.server.service.JobService;
import name.mrkandreev.ckpipeline.server.service.WebSocketSessionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@Log4j2
public class WebSocketConfig {
  @Bean
  public WebSocketEndpoint webSocketEndpoint(
      WebSocketSessionService webSocketSessionService, JobService backendService) {
    WebSocketEndpoint webSocketEndpoint = new WebSocketEndpoint();
    webSocketEndpoint.setWebSocketSessionService(webSocketSessionService);
    webSocketEndpoint.setJobService(backendService);
    return webSocketEndpoint;
  }

  @Bean
  @DependsOn("webSocketEndpoint")
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
}
