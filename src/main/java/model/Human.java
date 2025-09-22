package model;

public class Human {
  private String name;
  private String surname;
  private int age;
  private boolean isWorking;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public boolean isWorking() {
    return isWorking;
  }

  public void setWorking(boolean working) {
    isWorking = working;
  }
}
