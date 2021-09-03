package name.mrkandreev.ckpipeline.server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest
@ContextConfiguration(
    classes = {
      MainControllerAdviceValidationResponseTest.SpecialController.class,
      MainControllerAdvice.class,
    })
@AutoConfigureMockMvc(addFilters = false)
public class MainControllerAdviceValidationResponseTest {
  @Autowired protected MockMvc mockMvc;

  @Test
  void test() throws Exception {
    final long invalid_number = SpecialController.MIN_VALUE - 1;

    mockMvc
        .perform(get(String.format("/endpoint/%s", invalid_number)))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @RestController
  @RequestMapping("/endpoint")
  @Validated
  @Log4j2
  static class SpecialController {
    public static final long MIN_VALUE = 5;

    @GetMapping("{number}")
    public void endpoint(@Valid @Min(MIN_VALUE) @PathVariable long number) {
      log.info(number);
    }
  }
}
