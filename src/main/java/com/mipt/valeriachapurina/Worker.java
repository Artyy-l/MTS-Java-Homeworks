package com.mipt.valeriachapurina;

public abstract class Worker {
  public abstract void work(int number);
  public boolean goHome(String str1,  String str2){
    return str1.equals(str2);
  }
}
