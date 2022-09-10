package com.github.kaheero.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ApiErrors {

  @Getter
  List<String> erros;

  public ApiErrors(BindingResult bindingResult) {
    this.erros = new ArrayList<>();
    bindingResult.getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      this.erros.add(fieldName + " " + errorMessage);
    });
  }

  public ApiErrors(BusinessException exception) {
    this.erros = Collections.singletonList(exception.getMessage());
  }

}
