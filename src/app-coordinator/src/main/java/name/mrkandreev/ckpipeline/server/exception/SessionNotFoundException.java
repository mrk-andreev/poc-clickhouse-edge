package name.mrkandreev.ckpipeline.server.exception;

public class SessionNotFoundException extends RuntimeException {
  public SessionNotFoundException() {
    super("Session not found");
  }
}
