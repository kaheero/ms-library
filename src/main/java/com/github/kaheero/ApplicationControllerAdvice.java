package com.github.kaheero;

import com.github.kaheero.exceptions.ApiErrors;
import com.github.kaheero.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApplicationControllerAdvice {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
    BindingResult bindingResult = exception.getBindingResult();
    return new ApiErrors(bindingResult);
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ApiErrors handleValidationExceptions(BusinessException exception) {
    return new ApiErrors(exception);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiErrors> handleResponseStatusException(
      ResponseStatusException exception) {
    return new ResponseEntity<>(new ApiErrors(exception), exception.getStatus());
  }

}
