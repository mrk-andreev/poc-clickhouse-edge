package name.mrkandreev.ckpipeline.client.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import name.mrkandreev.ckpipeline.client.dto.JobRequestDto;
import name.mrkandreev.ckpipeline.client.dto.JobResponseDto;
import org.apache.camel.Exchange;
import org.apache.camel.component.exec.ExecBinding;
import org.apache.camel.component.exec.ExecCommand;
import org.apache.camel.component.exec.ExecResult;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class ExecHelperTest {
  private static final ObjectMapper mapper = new JsonMapper();

  @Test
  void prepareExec() throws Exception {
    final Path clickhousePath = Path.of("/clickhouse");
    final String query = UUID.randomUUID().toString();
    final String requestId = UUID.randomUUID().toString();
    final JobRequestDto backendRequest =
        JobRequestDto.builder().query(query).requestId(requestId).build();
    final Exchange exchange = new DefaultExchange(new DefaultCamelContext());
    exchange.getIn().setBody(mapper.writeValueAsString(backendRequest));

    ExecHelper.prepareExec(clickhousePath).process(exchange);

    assertEquals(
        requestId, exchange.getIn().getHeader(ExecHelper.BACKEND_REQUEST_ID_HEADER, String.class));
    assertEquals(
        clickhousePath.toString(),
        exchange.getIn().getHeader(ExecBinding.EXEC_COMMAND_EXECUTABLE, String.class));
    assertEquals(
        List.of("local", "--query", query),
        exchange.getIn().getHeader(ExecBinding.EXEC_COMMAND_ARGS, List.class));
  }

  @Test
  void collectExecResultsStderr() throws Exception {
    final String requestId = UUID.randomUUID().toString();
    final Exchange exchange = new DefaultExchange(new DefaultCamelContext());
    exchange.getIn().setHeader(ExecHelper.BACKEND_REQUEST_ID_HEADER, requestId);
    ExecCommand execCommand = mock(ExecCommand.class);
    final String errorText = UUID.randomUUID().toString();
    final ExecResult execResult =
        new ExecResult(
            execCommand, null, IOUtils.toInputStream(errorText, Charset.defaultCharset()), 0);
    exchange.getIn().setBody(execResult);

    ExecHelper.collectExecResults().process(exchange);

    assertEquals(
        JobResponseDto.builder().requestId(requestId).response(errorText).build(),
        mapper.readValue(exchange.getIn().getBody(String.class), JobResponseDto.class));
  }

  @Test
  void collectExecResultsStdout() throws Exception {
    final String requestId = UUID.randomUUID().toString();
    final Exchange exchange = new DefaultExchange(new DefaultCamelContext());
    exchange.getIn().setHeader(ExecHelper.BACKEND_REQUEST_ID_HEADER, requestId);
    ExecCommand execCommand = mock(ExecCommand.class);
    final String outputText = UUID.randomUUID().toString();
    final ExecResult execResult =
        new ExecResult(
            execCommand, IOUtils.toInputStream(outputText, Charset.defaultCharset()), null, 0);
    exchange.getIn().setBody(execResult);

    ExecHelper.collectExecResults().process(exchange);

    assertEquals(
        JobResponseDto.builder().requestId(requestId).response(outputText).build(),
        mapper.readValue(exchange.getIn().getBody(String.class), JobResponseDto.class));
  }
}
