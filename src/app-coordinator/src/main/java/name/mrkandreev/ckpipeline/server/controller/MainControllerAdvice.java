package name.mrkandreev.ckpipeline.server.controller;

import javax.validation.ConstraintViolationException;
import name.mrkandreev.ckpipeline.server.dto.ErrorMessageDto;
import name.mrkandreev.ckpipeline.server.exception.JobNotFound;
import name.mrkandreev.ckpipeline.server.exception.SessionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class MainControllerAdvice {
  @ExceptionHandler({SessionNotFoundException.class, JobNotFound.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorMessageDto notFoundException(RuntimeException e) {
    return ErrorMessageDto.builder()
        .type("SessionNotFoundException")
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorMessageDto constraintViolationException(ConstraintViolationException e) {
    return ErrorMessageDto.builder()
        .type("ConstraintViolationException")
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler(Throwable.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorMessageDto anyException(Throwable e) {
    return ErrorMessageDto.builder().type("Throwable").message(e.getMessage()).build();
  }
}
