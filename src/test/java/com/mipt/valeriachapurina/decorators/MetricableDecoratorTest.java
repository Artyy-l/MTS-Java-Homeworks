package com.mipt.valeriachapurina.decorators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricableDecoratorTest {
  @Test
  void worksWithMetrics() {
    DataService service = new MetricableDecorator(new SimpleDataService());
    service.saveData("k", "v");
    assertEquals("v", service.findDataByKey("k").orElseThrow());
    assertTrue(service.deleteData("k"));
  }
}