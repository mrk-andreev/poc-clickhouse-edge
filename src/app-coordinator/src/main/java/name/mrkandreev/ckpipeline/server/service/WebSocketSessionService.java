package name.mrkandreev.ckpipeline.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.websocket.Session;
import lombok.RequiredArgsConstructor;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.exception.JobCreationException;
import name.mrkandreev.ckpipeline.server.exception.SessionNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketSessionService {
  private static final ObjectMapper mapper = new JsonMapper();
  private final Map<String, Session> wsSessions;

  public void add(Session session) {
    wsSessions.put(session.getId(), session);
  }

  public void remove(Session session) {
    wsSessions.remove(session.getId());
  }

  public List<String> getSessionsId() {
    return new ArrayList<>(wsSessions.keySet());
  }

  @SuppressWarnings("PMD")
  public void send(String sessionId, JobDto job) {
    Session session = wsSessions.get(sessionId);
    if (session == null || !session.isOpen()) {
      throw new SessionNotFoundException();
    }

    try {
      session.getAsyncRemote().sendText(mapper.writeValueAsString(job)).get();
    } catch (ExecutionException | IOException e) {
      throw new JobCreationException(e);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void cleanClosedSessions() {
    wsSessions.entrySet().stream()
        .filter(s -> s.getValue() == null || !s.getValue().isOpen())
        .forEach(s -> wsSessions.remove(s.getKey()));
  }

  public void pingSessions() {
    wsSessions.values().parallelStream()
        .filter(Session::isOpen)
        .forEach(session -> session.getAsyncRemote().sendText(""));
  }
}
