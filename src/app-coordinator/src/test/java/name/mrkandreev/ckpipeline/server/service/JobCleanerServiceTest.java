package name.mrkandreev.ckpipeline.server.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import name.mrkandreev.ckpipeline.server.dao.JobDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobCleanerServiceTest {
  @Mock private JobDao dao;

  @InjectMocks private JobCleanerService jobCleanerService;

  @Test
  void invoke() {
    jobCleanerService.invoke();

    verify(dao, times(1)).collectOldJobs();
  }
}
