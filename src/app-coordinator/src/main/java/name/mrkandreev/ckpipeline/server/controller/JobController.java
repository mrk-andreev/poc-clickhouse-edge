package name.mrkandreev.ckpipeline.server.controller;

import lombok.RequiredArgsConstructor;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.service.JobService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Validated
public class JobController {
  private final JobService service;

  @GetMapping("{jobUuid}")
  public JobDto getJob(@PathVariable String jobUuid) {
    return service.getJob(jobUuid);
  }
}
