package name.mrkandreev.ckpipeline.server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.net.URI;
import java.util.UUID;
import javax.websocket.Session;
import name.mrkandreev.ckpipeline.server.dto.JobResultInput;
import name.mrkandreev.ckpipeline.server.service.JobService;
import name.mrkandreev.ckpipeline.server.service.WebSocketSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketEndpointTest {
  private static final ObjectMapper mapper = new JsonMapper();
  private static final WebSocketSessionService webSocketSessionService =
      mock(WebSocketSessionService.class);
  private static final JobService jobService = mock(JobService.class);

  @Value("${local.server.port}")
  private int port;

  @Test
  void test() throws Exception {
    final JobResultInput input =
        JobResultInput.builder()
            .requestId(UUID.randomUUID().toString())
            .response(UUID.randomUUID().toString())
            .build();
    final String message = mapper.writeValueAsString(input);

    WebSocketClient client = new StandardWebSocketClient();
    ListenableFuture<WebSocketSession> future =
        client.doHandshake(
            new AbstractWebSocketHandler() {
              @Override
              public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                super.afterConnectionEstablished(session);
                session.sendMessage(new TextMessage(message));
              }
            },
            null,
            URI.create(String.format("ws://localhost:%s/api/ws", port)));
    WebSocketSession session = future.completable().get();
    session.close();

    Thread.sleep(100); // fix verify

    verify(webSocketSessionService, atLeastOnce()).add(any(Session.class));
    verify(webSocketSessionService, atLeastOnce()).remove(any(Session.class));
    verify(jobService, times(1)).saveJobResult(input);
  }

  @Test
  void testError() throws Exception {
    final String message = UUID.randomUUID().toString();

    WebSocketClient client = new StandardWebSocketClient();
    ListenableFuture<WebSocketSession> future =
        client.doHandshake(
            new AbstractWebSocketHandler() {
              @Override
              public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                super.afterConnectionEstablished(session);
                session.sendMessage(new TextMessage(message));
              }
            },
            null,
            URI.create(String.format("ws://localhost:%s/api/ws", port)));
    WebSocketSession session = future.completable().get();
    session.close();

    Thread.sleep(100); // fix verify

    verify(webSocketSessionService, atLeastOnce()).add(any(Session.class));
    verify(webSocketSessionService, atLeastOnce()).remove(any(Session.class));
  }

  @TestConfiguration
  public static class WebSocketDependenciesConfig {
    @Bean
    public WebSocketSessionService webSocketSessionService() {
      return webSocketSessionService;
    }

    @Bean
    public JobService jobService() {
      return jobService;
    }
  }
}
