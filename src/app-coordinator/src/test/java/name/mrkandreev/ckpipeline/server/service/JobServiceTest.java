package name.mrkandreev.ckpipeline.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import name.mrkandreev.ckpipeline.server.dao.JobDao;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.dto.JobResultInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
  @Mock private WebSocketSessionService webSocketSessionService;

  @Mock private JobDao dao;

  @InjectMocks private JobService jobService;

  @Test
  void getSessions() {
    final List<String> sessions = List.of(UUID.randomUUID().toString());
    when(webSocketSessionService.getSessionsId()).thenReturn(sessions);

    assertEquals(jobService.getSessions(), sessions);
    verify(webSocketSessionService, times(1)).getSessionsId();
  }

  @Test
  void executeQuery() {
    final String session = UUID.randomUUID().toString();
    final String query = UUID.randomUUID().toString();

    String requestUuid = jobService.executeQuery(session, query);

    assertNotNull(requestUuid);
    verify(dao, times(1)).save(any(JobDto.class));
    verify(webSocketSessionService, times(1)).send(anyString(), any(JobDto.class));
    verify(
            dao,
            verificationData ->
                verificationData.getAllInvocations().stream()
                    .filter(invocation -> invocation.getMethod().getName().equals("save"))
                    .forEach(
                        invocation -> {
                          JobDto jobDto = (JobDto) invocation.getArguments()[0];

                          assertFalse(jobDto.isResolved());
                          assertEquals(requestUuid, jobDto.getRequestId());
                        }))
        .save(any(JobDto.class));
  }

  @Test
  void getJob() {
    final String requestId = UUID.randomUUID().toString();
    final JobDto jobDto = JobDto.builder().isResolved(false).requestId(requestId).build();

    when(dao.get(requestId)).thenReturn(jobDto);

    assertEquals(jobDto, jobService.getJob(requestId));
  }

  @Test
  void saveJobResult() {
    final String requestId = UUID.randomUUID().toString();
    final String response = UUID.randomUUID().toString();
    final JobResultInput input =
        JobResultInput.builder().requestId(requestId).response(response).build();
    final JobDto jobDto = JobDto.builder().requestId(requestId).isResolved(false).build();
    when(dao.get(requestId)).thenReturn(jobDto);

    jobService.saveJobResult(input);

    verify(dao, times(1)).get(requestId);
    verify(dao, times(1)).update(any(JobDto.class));
    verify(
            dao,
            verificationData ->
                verificationData.getAllInvocations().stream()
                    .filter(invocation -> invocation.getMethod().getName().equals("update"))
                    .forEach(
                        invocation -> {
                          JobDto updatedJobDto = (JobDto) invocation.getArguments()[0];

                          assertTrue(updatedJobDto.isResolved());
                          assertEquals(response, updatedJobDto.getResponse());
                        }))
        .update(any(JobDto.class));
  }
}
