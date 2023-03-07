package de.hhu.propra.splitter.web;

import de.hhu.propra.splitter.exceptions.GruppeNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandlerAdvice {

  @ExceptionHandler(GruppeNotFound.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleException(GruppeNotFound exception) {
    return "error/404";
  }
}