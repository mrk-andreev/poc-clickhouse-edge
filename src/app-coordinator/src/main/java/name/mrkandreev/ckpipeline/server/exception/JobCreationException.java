package name.mrkandreev.ckpipeline.server.exception;

public class JobCreationException extends RuntimeException {
  public JobCreationException(Throwable e) {
    super(e);
  }
}
