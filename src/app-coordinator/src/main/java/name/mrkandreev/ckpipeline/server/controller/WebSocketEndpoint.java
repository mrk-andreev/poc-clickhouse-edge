package name.mrkandreev.ckpipeline.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import name.mrkandreev.ckpipeline.server.dto.JobResultInput;
import name.mrkandreev.ckpipeline.server.service.JobService;
import name.mrkandreev.ckpipeline.server.service.WebSocketSessionService;

@ServerEndpoint("/ws")
@RequiredArgsConstructor
@Log4j2
public class WebSocketEndpoint {
  private static final ObjectMapper mapper = new JsonMapper();
  private static WebSocketSessionService webSocketSessionService;
  private static JobService jobService;

  public void setWebSocketSessionService(WebSocketSessionService webSocketSessionService) {
    WebSocketEndpoint.webSocketSessionService = webSocketSessionService;
  }

  public void setJobService(JobService backendService) {
    WebSocketEndpoint.jobService = backendService;
  }

  @OnOpen
  public void onOpen(Session session) {
    webSocketSessionService.add(session);
  }

  @OnClose
  public void onClose(Session session) {
    webSocketSessionService.remove(session);
  }

  @OnError
  public void onError(Session session, Throwable e) {
    log.info(session);
    log.error(e);
  }

  @OnMessage
  public void onMessage(String message) throws Exception {
    jobService.saveJobResult(mapper.readValue(message, JobResultInput.class));
  }
}
