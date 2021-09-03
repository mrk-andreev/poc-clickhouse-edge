package name.mrkandreev.ckpipeline.client.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import name.mrkandreev.ckpipeline.client.dto.JobRequestDto;
import name.mrkandreev.ckpipeline.client.dto.JobResponseDto;
import org.apache.camel.Processor;
import org.apache.camel.component.exec.ExecBinding;
import org.apache.camel.component.exec.ExecResult;
import org.apache.commons.io.IOUtils;

public final class ExecHelper {
  public static final String BACKEND_REQUEST_ID_HEADER = "BACKEND_REQUEST_ID";
  private static final ObjectMapper mapper = new JsonMapper();

  private ExecHelper() {
    // empty
  }

  public static Processor prepareExec(Path clickhouseExecutablePath) {
    return exchange -> {
      JobRequestDto backendRequest =
          mapper.readValue(exchange.getIn().getBody(String.class), JobRequestDto.class);

      exchange
          .getIn()
          .setHeader(ExecBinding.EXEC_COMMAND_EXECUTABLE, clickhouseExecutablePath.toString());
      exchange
          .getIn()
          .setHeader(
              ExecBinding.EXEC_COMMAND_ARGS,
              List.of("local", "--query", backendRequest.getQuery()));
      exchange.getIn().setHeader(BACKEND_REQUEST_ID_HEADER, backendRequest.getRequestId());
    };
  }

  public static Processor collectExecResults() {
    return exchange ->
        exchange
            .getIn()
            .setBody(
                mapper.writeValueAsString(
                    JobResponseDto.builder()
                        .requestId(
                            exchange.getIn().getHeader(BACKEND_REQUEST_ID_HEADER, String.class))
                        .response(extractResponse(exchange.getIn().getBody(ExecResult.class)))
                        .build()));
  }

  private static String extractResponse(ExecResult execResult) throws IOException {
    if (execResult.getStderr() != null) {
      return IOUtils.toString(execResult.getStderr(), StandardCharsets.UTF_8);
    }

    return IOUtils.toString(execResult.getStdout(), StandardCharsets.UTF_8);
  }
}
