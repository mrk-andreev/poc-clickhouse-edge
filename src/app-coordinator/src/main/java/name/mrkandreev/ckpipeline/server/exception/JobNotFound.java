package name.mrkandreev.ckpipeline.server.exception;

public class JobNotFound extends RuntimeException {
  public JobNotFound(String requestUuid) {
    super(String.format("Job with requestUuid = '%s' not found", requestUuid));
  }
}
