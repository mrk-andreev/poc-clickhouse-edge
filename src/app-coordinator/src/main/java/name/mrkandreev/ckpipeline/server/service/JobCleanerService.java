package name.mrkandreev.ckpipeline.server.service;

import lombok.RequiredArgsConstructor;
import name.mrkandreev.ckpipeline.server.dao.JobDao;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Profile("!no_job_cleaner")
@RequiredArgsConstructor
public class JobCleanerService {
  private final JobDao jobService;

  @Scheduled(fixedRateString = "${job-clean-up.scan-timeout-ms}")
  public void invoke() {
    jobService.collectOldJobs();
  }
}
