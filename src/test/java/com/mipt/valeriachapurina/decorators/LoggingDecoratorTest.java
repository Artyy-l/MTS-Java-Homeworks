package com.mipt.valeriachapurina.decorators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggingDecoratorTest {
  @Test
  void doesNotBreakLogic() {
    DataService service = new LoggingDecorator(new SimpleDataService());
    service.saveData("k", "v");
    assertTrue(service.findDataByKey("k").isPresent());
    assertTrue(service.deleteData("k"));
  }
}