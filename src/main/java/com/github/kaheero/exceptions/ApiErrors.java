package com.github.kaheero.exceptions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ApiErrors {

  @Getter
  List<String> erros;

  public ApiErrors(BindingResult bindingResult) {
    this.erros = new ArrayList<>();
    bindingResult.getAllErrors().forEach(erro -> {
      String fieldName = ((FieldError) erro).getField();
      String errorMessage = erro.getDefaultMessage();
      this.erros.add(fieldName + " " + errorMessage);
    });
  }

  public ApiErrors(BussinessException exception) {
    this.erros = List.of(exception.getMessage());
  }

}
