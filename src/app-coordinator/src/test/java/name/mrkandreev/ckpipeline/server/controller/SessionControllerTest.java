package name.mrkandreev.ckpipeline.server.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
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
class SessionControllerTest {
  @Autowired protected MockMvc mockMvc;

  @MockBean private JobService service;

  @Test
  void getSessions() throws Exception {
    final List<String> sessions = List.of(UUID.randomUUID().toString());
    when(service.getSessions()).thenReturn(sessions);

    mockMvc
        .perform(get("/sessions"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0]").value(sessions.get(0)));

    verify(service, times(1)).getSessions();
  }

  @Test
  void executeQuery() throws Exception {
    final String session = UUID.randomUUID().toString();
    final String query = UUID.randomUUID().toString();
    final String requestUuid = UUID.randomUUID().toString();

    when(service.executeQuery(session, query)).thenReturn(requestUuid);

    mockMvc
        .perform(post(String.format("/sessions/%s/execute", session)).content(query))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(requestUuid));

    verify(service, times(1)).executeQuery(session, query);
  }
}
