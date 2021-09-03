package name.mrkandreev.ckpipeline.server.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import name.mrkandreev.ckpipeline.server.dao.JobDao;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.dto.JobResultInput;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class JobService {
  private final WebSocketSessionService webSocketSessionService;
  private final JobDao dao;

  public List<String> getSessions() {
    return webSocketSessionService.getSessionsId();
  }

  public String executeQuery(String session, String query) {
    String requestUuid = UUID.randomUUID().toString();
    JobDto job =
        JobDto.builder()
            .createdAt(Instant.now().getEpochSecond())
            .isResolved(false)
            .requestId(requestUuid)
            .query(query)
            .build();

    webSocketSessionService.send(session, job);
    dao.save(job);

    return requestUuid;
  }

  public JobDto getJob(String requestUuid) {
    return dao.get(requestUuid);
  }

  public void saveJobResult(JobResultInput value) {
    JobDto request = dao.get(value.getRequestId());
    request.setResolved(true);
    request.setResponse(value.getResponse());
    dao.update(request);
  }
}
