package de.hhu.propra.splitter.web;

import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlerAdvice {

  @ExceptionHandler(GruppeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleGruppeNotFoundException(GruppeNotFoundException exception) {
    return "error/404";
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleMissingServletRequestParameterException(GruppeNotFoundException exception) {
    return "error/404";
  }
}