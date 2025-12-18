package com.mipt.valeriachapurina.decorators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationDecoratorTest {
  @Test
  void throwsOnInvalidKey() {
    DataService service = new ValidationDecorator(new SimpleDataService());
    assertThrows(IllegalArgumentException.class, () -> service.findDataByKey(""));
  }

  @Test
  void throwsOnInvalidData() {
    DataService service = new ValidationDecorator(new SimpleDataService());
    assertThrows(IllegalArgumentException.class, () -> service.saveData("k", " "));
  }
}
