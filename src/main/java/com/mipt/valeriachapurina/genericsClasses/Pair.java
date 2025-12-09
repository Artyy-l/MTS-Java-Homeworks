package com.mipt.valeriachapurina.genericsClasses;

public class Pair<K, V> {
  private K key;
  private V value;

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public V getValue() {
    return value;
  }

  public void setValue(V value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Pair{key=" +
            (key == null ? "null" : key) +
            ", value=" +
            (value == null ? "null" : value)
            + "}";
  }

  public Pair<V, K> swap() {
    return new Pair<V, K>(this.value, this.key);
  }
}
