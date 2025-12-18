package com.mipt.valeriachapurina.decorators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CachingDecoratorTest {
  @Test
  void cachesValueAfterFirstCall() {
    DataService service = new CachingDecorator(new SimpleDataService());
    service.saveData("a", "1");
    assertEquals("1", service.findDataByKey("a").orElseThrow());
    assertEquals("1", service.findDataByKey("a").orElseThrow());
  }
}