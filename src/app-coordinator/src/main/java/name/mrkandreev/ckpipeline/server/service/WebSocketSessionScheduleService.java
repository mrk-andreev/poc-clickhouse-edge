package name.mrkandreev.ckpipeline.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("!no_ws_cleaner")
@RequiredArgsConstructor
public class WebSocketSessionScheduleService {
  private final WebSocketSessionService webSocketSessionService;

  @Scheduled(fixedRateString = "${web-socket.clean-timeout-ms}")
  public void invokeClean() {
    webSocketSessionService.cleanClosedSessions();
  }

  @Scheduled(fixedRateString = "${web-socket.ping-timeout-ms}")
  public void invokePing() {
    webSocketSessionService.pingSessions();
  }
}
