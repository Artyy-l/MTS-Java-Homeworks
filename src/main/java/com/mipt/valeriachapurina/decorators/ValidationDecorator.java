package com.mipt.valeriachapurina.decorators;

import java.util.Optional;

public class ValidationDecorator extends AbstractDataServiceDecorator {
  public ValidationDecorator(DataService delegate) {
    super(delegate);
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    validateKey(key);
    return delegate.findDataByKey(key);
  }

  @Override
  public void saveData(String key, String data) {
    validateKey(key);
    if (data == null || data.isBlank()) {
      throw new IllegalArgumentException("Data must not be null or blank");
    }
    delegate.saveData(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    validateKey(key);
    return delegate.deleteData(key);
  }

  private void validateKey(String key) {
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException("Key must not be null or blank");
    }
  }
}