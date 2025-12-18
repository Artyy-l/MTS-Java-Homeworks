package com.mipt.valeriachapurina.decorators;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CachingDecorator extends AbstractDataServiceDecorator {
  private final Map<String, Optional<String>> cache = new HashMap<>();

  public CachingDecorator(DataService delegate) {
    super(delegate);
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    if (cache.containsKey(key)) {
      return cache.get(key);
    }
    Optional<String> result = delegate.findDataByKey(key);
    cache.put(key, result);
    return result;
  }

  @Override
  public void saveData(String key, String data) {
    delegate.saveData(key, data);
    cache.put(key, Optional.ofNullable(data));
  }

  @Override
  public boolean deleteData(String key) {
    cache.remove(key);
    return delegate.deleteData(key);
  }
}
