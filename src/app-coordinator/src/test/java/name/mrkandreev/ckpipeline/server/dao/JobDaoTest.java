package name.mrkandreev.ckpipeline.server.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.exception.JobNotFound;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JobDaoTest {
  @Mock private Map<String, JobDto> jobs;

  @InjectMocks JobDao dao;

  @Test
  void save() {
    final String requestUuid = UUID.randomUUID().toString();
    final JobDto job = JobDto.builder().requestId(requestUuid).build();

    dao.save(job);

    verify(jobs, times(1)).put(requestUuid, job);
  }

  @Test
  void get() {
    final String requestUuid = UUID.randomUUID().toString();
    final JobDto job = JobDto.builder().requestId(requestUuid).build();
    when(jobs.get(requestUuid)).thenReturn(job);

    assertEquals(job, dao.get(requestUuid));
  }

  @Test
  void getNotExists() {
    final String requestUuid = UUID.randomUUID().toString();
    when(jobs.get(requestUuid)).thenReturn(null);

    assertThrows(JobNotFound.class, () -> dao.get(requestUuid));
  }

  @Test
  void update() {
    final String requestUuid = UUID.randomUUID().toString();
    final JobDto job = JobDto.builder().requestId(requestUuid).build();
    when(jobs.containsKey(requestUuid)).thenReturn(true);

    dao.update(job);

    verify(jobs, times(1)).put(requestUuid, job);
  }

  @Test
  void updateNotExistsJob() {
    final String requestUuid = UUID.randomUUID().toString();
    final JobDto job = JobDto.builder().requestId(requestUuid).build();
    when(jobs.containsKey(requestUuid)).thenReturn(false);

    assertThrows(JobNotFound.class, () -> dao.update(job));
  }

  @Test
  void collectOldJobs() {
    dao.collectOldJobs();

    verify(jobs, times(1)).forEach(any());
  }

  @Test
  void deleteIfOld() {
    final long jobMaxAge = 1000L;
    final Instant now = Instant.now();
    final String requestUuid = UUID.randomUUID().toString();
    final JobDto job =
        JobDto.builder()
            .requestId(requestUuid)
            .createdAt(now.minus(jobMaxAge + 1L, ChronoUnit.MILLIS).getEpochSecond())
            .build();
    ReflectionTestUtils.setField(dao, "jobMaxAge", jobMaxAge);

    dao.deleteIfOld(job, now);

    verify(jobs, times(1)).remove(anyString());
  }

  @Test
  void deleteIfOldNotOld() {
    final Instant now = Instant.now();
    final String requestUuid = UUID.randomUUID().toString();
    final JobDto job =
        JobDto.builder().requestId(requestUuid).createdAt(now.getEpochSecond()).build();
    ReflectionTestUtils.setField(dao, "jobMaxAge", 1000L);

    dao.deleteIfOld(job, now);

    verify(jobs, times(0)).remove(anyString());
  }
}
