package com.mipt.valeriachapurina.reflections_and_annotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidatorTest {
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
  }

  @Test
  void testUserIsValid() {
    user.setName("Lera");
    user.setEmail("lera@gmail.ru");
    user.setAge(18);
    user.setPassword("password");

    ValidationResult result = Validator.validate(user);
    assertTrue(result.isValid());
    assertEquals(0, result.getErrors().size());
  }

  @Test
  void testNullNameIsNotValid() {
    user.setName(null);
    ValidationResult result = Validator.validate(user);
    assertFalse(result.isValid());
    assertTrue(result.getErrors().contains("Имя не может быть null"));
    assertTrue(result.getErrors().contains("Имя должно быть от 2 до 50 символов"));
  }

  @Test
  void testLongNameIsNotValid() {
    user.setName("A".repeat(52));
    ValidationResult result = Validator.validate(user);
    assertFalse(result.isValid());
    assertTrue(result.getErrors().contains("Имя должно быть от 2 до 50 символов"));
  }

  @Test
  void testShortNameIsNotValid() {
    user.setName("A");
    ValidationResult result = Validator.validate(user);
    assertFalse(result.isValid());
    assertTrue(result.getErrors().contains("Имя должно быть от 2 до 50 символов"));
  }

  @Test
  void testNullEmailIsNotValid() {
    user.setEmail(null);
    ValidationResult result = Validator.validate(user);
    assertFalse(result.isValid());
    assertTrue(result.getErrors().contains("Email не может быть null"));
    assertTrue(result.getErrors().contains("Некорректный формат email"));
  }

  @Test
  void testIncorrectEmailIsNotValid() {
    user.setEmail("lera@gmail");
    ValidationResult result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Некорректный формат email"));

    user.setEmail("");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Некорректный формат email"));

    user.setEmail("@gmail.ru");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Некорректный формат email"));

    user.setEmail("lera");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Некорректный формат email"));

    user.setEmail("lera.lera");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Некорректный формат email"));

    user.setEmail("lera@.ru");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Некорректный формат email"));
  }

  @Test
  void testNullAgeIsNotValid() {
    user.setAge(null);
    ValidationResult result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));

    user.setAge(-1000);
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));

    user.setAge(1000);
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));

    user.setAge(-1);
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));

    user.setAge(151);
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));
  }

  @Test
  void testPasswordIsNotValid() {
    user.setPassword(null);
    ValidationResult result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Пароль должен быть от 6 до 20 символов"));

    user.setPassword("");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Пароль должен быть от 6 до 20 символов"));

    user.setPassword("pass");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Пароль должен быть от 6 до 20 символов"));

    user.setPassword("password_password_password_password");
    result = Validator.validate(user);
    assertTrue(result.getErrors().contains("Пароль должен быть от 6 до 20 символов"));
  }

  @Test
  void testMultipleErrors() {
    user = new User("A", "lera", 1000, "pass");
    ValidationResult result = Validator.validate(user);

    assertFalse(result.isValid());
    assertEquals(4, result.getErrors().size());
    assertTrue(result.getErrors().contains("Имя должно быть от 2 до 50 символов"));
    assertTrue(result.getErrors().contains("Некорректный формат email"));
    assertTrue(result.getErrors().contains("Возраст должен быть от 0 до 150"));
    assertTrue(result.getErrors().contains("Пароль должен быть от 6 до 20 символов"));
  }

  @Test
  void nullObject_ShouldThrowException() {
    assertThrows(NullPointerException.class, () -> {
      Validator.validate(null);
    });
  }
}
