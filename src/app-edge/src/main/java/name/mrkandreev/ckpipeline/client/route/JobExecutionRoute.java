package name.mrkandreev.ckpipeline.client.route;

import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import name.mrkandreev.ckpipeline.client.helper.ExecHelper;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobExecutionRoute extends RouteBuilder {
  private final Path clickhouseExecutablePath;

  @Value("${master-server.protocol}")
  private String masterServerProtocol;

  @Value("${master-server.endpoint}")
  private String masterServerEndpoint;

  @Override
  public void configure() {
    from(String.format("ahc-%s://%s", masterServerProtocol, masterServerEndpoint))
        .log("info ${body}")
        .filter(exchange -> exchange.getIn().getBody(String.class).length() > 0)
        .process(ExecHelper.prepareExec(clickhouseExecutablePath))
        .to("exec:placeholder")
        .process(ExecHelper.collectExecResults())
        .to(String.format("ahc-%s://%s", masterServerProtocol, masterServerEndpoint));
  }
}
