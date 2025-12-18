package com.mipt.valeriachapurina.decorators;

import java.util.Optional;

public class LoggingDecorator extends AbstractDataServiceDecorator {
  public LoggingDecorator(DataService delegate) {
    super(delegate);
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    System.out.println("findDataByKey: " + key);
    return delegate.findDataByKey(key);
  }

  @Override
  public void saveData(String key, String data) {
    System.out.println("saveData: " + key);
    delegate.saveData(key, data);
  }

  @Override
  public boolean deleteData(String key) {
    System.out.println("deleteData: " + key);
    return delegate.deleteData(key);
  }
}
