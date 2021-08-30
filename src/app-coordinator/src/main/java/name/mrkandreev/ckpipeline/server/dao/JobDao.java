package name.mrkandreev.ckpipeline.server.dao;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.exception.JobNotFound;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobDao {
  private final Map<String, JobDto> jobs;

  @Value("${job-clean-up.max-age-ms}")
  private long jobMaxAge;

  public void save(JobDto job) {
    jobs.put(job.getRequestId(), job);
  }

  public JobDto get(String requestUuid) {
    JobDto jobDto = jobs.get(requestUuid);
    if (jobDto == null) {
      throw new JobNotFound(requestUuid);
    }

    return jobDto;
  }

  public void update(JobDto job) {
    if (!jobs.containsKey(job.getRequestId())) {
      throw new JobNotFound(job.getRequestId());
    }

    jobs.put(job.getRequestId(), job);
  }

  public void collectOldJobs() {
    Instant now = Instant.now();
    jobs.forEach((s, jobDto) -> deleteIfOld(jobDto, now));
  }

  public void deleteIfOld(JobDto job, Instant now) {
    Duration jobAge = Duration.between(Instant.ofEpochSecond(job.getCreatedAt()), now);
    if (jobAge.toMillis() > jobMaxAge) {
      jobs.remove(job.getRequestId());
    }
  }
}
