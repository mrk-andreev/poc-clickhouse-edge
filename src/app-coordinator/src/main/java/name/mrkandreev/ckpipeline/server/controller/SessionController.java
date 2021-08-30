package name.mrkandreev.ckpipeline.server.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import name.mrkandreev.ckpipeline.server.service.JobService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Validated
public class SessionController {
  private final JobService service;

  @GetMapping
  public List<String> getSessions() {
    return service.getSessions();
  }

  @PostMapping("{session}/execute")
  public String executeQuery(
      @PathVariable String session, @RequestBody @Valid @NotNull String query) {
    return service.executeQuery(session, query);
  }
}
