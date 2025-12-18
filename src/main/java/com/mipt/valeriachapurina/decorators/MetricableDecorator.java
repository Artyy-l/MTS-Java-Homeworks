package com.mipt.valeriachapurina.decorators;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class MetricableDecorator extends AbstractDataServiceDecorator {
  private final MetricService metricService = new MetricService();

  public MetricableDecorator(DataService delegate) {
    super(delegate);
  }

  @Override
  public Optional<String> findDataByKey(String key) {
    Instant start = Instant.now();
    try {
      return delegate.findDataByKey(key);
    } finally {
      metricService.sendMetric(Duration.between(start, Instant.now()));
    }
  }

  @Override
  public void saveData(String key, String data) {
    Instant start = Instant.now();
    try {
      delegate.saveData(key, data);
    } finally {
      metricService.sendMetric(Duration.between(start, Instant.now()));
    }
  }

  @Override
  public boolean deleteData(String key) {
    Instant start = Instant.now();
    try {
      return delegate.deleteData(key);
    } finally {
      metricService.sendMetric(Duration.between(start, Instant.now()));
    }
  }

  public static class MetricService {
    public void sendMetric(Duration duration) {
      System.out.println("Метод выполнялся: " + duration);
    }
  }
}
