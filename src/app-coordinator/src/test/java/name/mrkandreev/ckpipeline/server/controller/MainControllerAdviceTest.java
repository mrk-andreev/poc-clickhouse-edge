package name.mrkandreev.ckpipeline.server.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import name.mrkandreev.ckpipeline.server.exception.SessionNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest
@ContextConfiguration(
    classes = {
      MainControllerAdviceTest.SpecialService.class,
      MainControllerAdviceTest.SpecialController.class,
      MainControllerAdvice.class,
    })
@AutoConfigureMockMvc(addFilters = false)
class MainControllerAdviceTest {
  @MockBean private SpecialService specialService;

  @Autowired protected MockMvc mockMvc;

  @Test
  void test() throws Exception {
    mockMvc.perform(get("/endpoint")).andDo(print()).andExpect(status().isOk());

    verify(specialService, times(1)).invoke();
  }

  @Test
  void sessionNotFoundException() throws Exception {
    doThrow(SessionNotFoundException.class).when(specialService).invoke();

    mockMvc.perform(get("/endpoint")).andDo(print()).andExpect(status().isNotFound());

    verify(specialService, times(1)).invoke();
  }

  @Test
  void throwable() throws Exception {
    doThrow(RuntimeException.class).when(specialService).invoke();

    mockMvc.perform(get("/endpoint")).andDo(print()).andExpect(status().isInternalServerError());

    verify(specialService, times(1)).invoke();
  }

  @Service
  interface SpecialService {
    void invoke() throws Exception;
  }

  @RestController
  @RequestMapping("/endpoint")
  static class SpecialController {
    @Autowired SpecialService specialService;

    @GetMapping
    public void endpoint() throws Exception {
      specialService.invoke();
    }
  }
}
