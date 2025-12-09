package com.mipt.valeriachapurina.collections;

import java.util.Objects;

public class Student {
  private int id;
  private String name;
  private double grade;

  public Student(int id, String name, double grade) {
    this.id = id;

    if (name == null) {
      throw new NullPointerException("name cannot be null");
    }
    this.name = name;

    if (grade < 0) {
      throw new IllegalArgumentException("grade cannot be negative");
    }
    this.grade = grade;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getGrade() {
    return grade;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Student student = (Student) o;
    return (id == student.id) && (Objects.equals(name, student.name)) && (Double.compare(grade, student.grade) == 0);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, grade);
  }
}
