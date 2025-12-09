package com.mipt.valeriachapurina.genericsClasses;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {
  /// объединяет две коллекции в одну новую. Коллекции из аргументов не должны измениться
  public static <T> List<T> mergeLists(List<? extends T> list1, List<? extends T> list2) {
    List<T> result = new ArrayList<>();
    if  (list1 != null) {
      result.addAll(list1);
    }

    if  (list2 != null) {
      result.addAll(list2);
    }

    return result;
  }

  /// Добавляет все элементы из source в destination
  public static <T> void addAll(List<? super T> destination, List<? extends T> source) {
    if (destination != null && source != null) {
      destination.addAll(source);
    }
  }
}
