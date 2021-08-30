package name.mrkandreev.ckpipeline.server.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.exception.JobCreationException;
import name.mrkandreev.ckpipeline.server.exception.SessionNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WebSocketSessionServiceTest {
  private static final ObjectMapper mapper = new JsonMapper();

  @Mock private Map<String, Session> wsSessions;

  @InjectMocks protected WebSocketSessionService service;

  @Test
  void add() {
    final String sessionId = UUID.randomUUID().toString();
    final Session session = mock(Session.class);
    when(session.getId()).thenReturn(sessionId);

    service.add(session);

    verify(wsSessions, times(1)).put(sessionId, session);
  }

  @Test
  void remove() {
    final String sessionId = UUID.randomUUID().toString();
    final Session session = mock(Session.class);
    when(session.getId()).thenReturn(sessionId);

    service.remove(session);

    verify(wsSessions, times(1)).remove(sessionId);
  }

  @Test
  void getSessionsId() {
    final Set<String> ids = Set.of(UUID.randomUUID().toString());
    when(wsSessions.keySet()).thenReturn(ids);

    Assertions.assertEquals(ids, new HashSet<>(service.getSessionsId()));
  }

  @Test
  void send() throws Exception {
    final String sessionId = UUID.randomUUID().toString();
    final Session session = mock(Session.class);
    final JobDto job = JobDto.builder().query(UUID.randomUUID().toString()).build();
    when(wsSessions.get(sessionId)).thenReturn(session);
    when(session.isOpen()).thenReturn(true);
    RemoteEndpoint.Async async = mock(RemoteEndpoint.Async.class);
    when(session.getAsyncRemote()).thenReturn(async);
    Future<Void> future =
        new MockFuture() {
          @Override
          public Void get() {
            return null;
          }
        };
    when(async.sendText(anyString())).thenReturn(future);

    service.send(sessionId, job);

    verify(async, times(1)).sendText(mapper.writeValueAsString(job));
  }

  @Test
  void sendWithInterruptedException() throws Exception {
    final String sessionId = UUID.randomUUID().toString();
    final Session session = mock(Session.class);
    final JobDto job = JobDto.builder().query(UUID.randomUUID().toString()).build();
    when(wsSessions.get(sessionId)).thenReturn(session);
    when(session.isOpen()).thenReturn(true);
    RemoteEndpoint.Async async = mock(RemoteEndpoint.Async.class);
    when(session.getAsyncRemote()).thenReturn(async);
    Future<Void> future =
        new MockFuture() {
          @Override
          public Void get() throws InterruptedException {
            throw new InterruptedException("");
          }
        };
    when(async.sendText(anyString())).thenReturn(future);

    service.send(sessionId, job);

    verify(async, times(1)).sendText(mapper.writeValueAsString(job));
  }

  @Test
  void sendWithUnknownSession() {
    final String sessionId = UUID.randomUUID().toString();
    final JobDto job = JobDto.builder().query(UUID.randomUUID().toString()).build();
    when(wsSessions.get(sessionId)).thenReturn(null);

    assertThrows(SessionNotFoundException.class, () -> service.send(sessionId, job));
  }

  @Test
  void sendWithClosedSession() {
    final String sessionId = UUID.randomUUID().toString();
    final Session session = mock(Session.class);
    final JobDto job = JobDto.builder().query(UUID.randomUUID().toString()).build();
    when(wsSessions.get(sessionId)).thenReturn(session);
    when(session.isOpen()).thenReturn(false);

    assertThrows(SessionNotFoundException.class, () -> service.send(sessionId, job));
  }

  @Test
  void sendCanNotSendToSession() {
    final String sessionId = UUID.randomUUID().toString();
    final Session session = mock(Session.class);
    final JobDto job = JobDto.builder().query(UUID.randomUUID().toString()).build();
    when(wsSessions.get(sessionId)).thenReturn(session);
    when(session.isOpen()).thenReturn(true);
    final RemoteEndpoint.Async async = mock(RemoteEndpoint.Async.class);
    when(session.getAsyncRemote()).thenReturn(async);
    final Future<Void> future =
        new MockFuture() {
          @Override
          public Void get() throws ExecutionException {
            throw new ExecutionException(new RuntimeException());
          }
        };
    when(async.sendText(anyString())).thenReturn(future);

    assertThrows(JobCreationException.class, () -> service.send(sessionId, job));
  }

  abstract static class MockFuture implements Future<Void> {
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return false;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return false;
    }

    @Override
    public abstract Void get() throws InterruptedException, ExecutionException;

    @Override
    public Void get(long timeout, TimeUnit unit) {
      return null;
    }
  }
}
