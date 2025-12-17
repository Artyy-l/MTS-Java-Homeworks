package com.mipt.valeriachapurina.reflections_and_annotations;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
  private boolean isValid;
  private final List<String> errors;

  public ValidationResult() {
    this.isValid = true;
    this.errors = new ArrayList<>();
  }

  public ValidationResult(boolean isValid, List<String> errors) {
    this.isValid = isValid;
    this.errors = errors;
  }

  public void addError(String errorMessage) {
    this.errors.add(errorMessage);
    this.isValid = false;
  }

  public List<String> getErrors() {
    return errors;
  }

  public boolean isValid() {
    return isValid;
  }
}