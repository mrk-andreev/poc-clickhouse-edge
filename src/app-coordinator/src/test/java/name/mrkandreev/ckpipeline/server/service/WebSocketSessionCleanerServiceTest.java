package name.mrkandreev.ckpipeline.server.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebSocketSessionCleanerServiceTest {
  @Mock private WebSocketSessionService webSocketSessionService;

  @InjectMocks private WebSocketSessionScheduleService webSocketSessionScheduleService;

  @Test
  void invokeClean() {
    webSocketSessionScheduleService.invokeClean();

    verify(webSocketSessionService, times(1)).cleanClosedSessions();
  }

  @Test
  void invokePing() {
    webSocketSessionScheduleService.invokePing();

    verify(webSocketSessionService, times(1)).pingSessions();
  }
}
