package com.github.kaheero.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;

public class ApiErrors {

  @Getter
  List<String> errors;

  public ApiErrors(BindingResult bindingResult) {
    this.errors = new ArrayList<>();
    bindingResult.getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      this.errors.add(fieldName + " " + errorMessage);
    });
  }

  public ApiErrors(ResponseStatusException exception) {
    this.errors = Collections.singletonList(exception.getReason());
  }

  public ApiErrors(BusinessException exception) {
    this.errors = Collections.singletonList(exception.getMessage());
  }

}
