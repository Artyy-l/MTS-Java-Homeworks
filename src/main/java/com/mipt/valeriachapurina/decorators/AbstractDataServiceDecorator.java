package com.mipt.valeriachapurina.decorators;

import java.util.Optional;

public abstract class AbstractDataServiceDecorator implements DataService {
  protected final DataService delegate;

  protected AbstractDataServiceDecorator(DataService delegate) {
    this.delegate = delegate;
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    return delegate.findDataByKey(key);
  }

  @Override
  public void saveData(String key, String data) {
    delegate.saveData(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    return delegate.deleteData(key);
  }
}
