package name.mrkandreev.ckpipeline.server.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;
import name.mrkandreev.ckpipeline.server.dto.JobDto;
import name.mrkandreev.ckpipeline.server.service.JobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class JobControllerTest {
  @Autowired protected MockMvc mockMvc;

  @MockBean private JobService service;

  @Test
  void getJob() throws Exception {
    final String jobUuid = UUID.randomUUID().toString();
    final long createdAt = Instant.now().getEpochSecond();
    final boolean isResolved = false;
    final String query = UUID.randomUUID().toString();

    when(service.getJob(jobUuid))
        .thenReturn(
            JobDto.builder()
                .requestId(jobUuid)
                .createdAt(createdAt)
                .isResolved(isResolved)
                .query(query)
                .build());

    mockMvc
        .perform(get(String.format("/jobs/%s", jobUuid)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.requestId").value(jobUuid))
        .andExpect(jsonPath("$.createdAt").value(createdAt))
        .andExpect(jsonPath("$.resolved").value(isResolved))
        .andExpect(jsonPath("$.query").value(query));

    verify(service, times(1)).getJob(jobUuid);
  }

  @Test
  void getJobWithoutIdShouldFail() throws Exception {
    mockMvc.perform(get("/jobs")).andDo(print()).andExpect(status().isNotFound());
  }
}
