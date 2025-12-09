package com.mipt.valeriachapurina.genericsClasses;

public class ArrayUtils {

  /// Поиск элемента в массиве
  /// @param array массив, в котором ищем элемент
  /// @param element искомый элемент
  /// @return индекс первого вхождения элемента или -1, если элемент не найден
  public static <T> int findFirst(T[] array, T element) {
    if (array == null || array.length == 0) {
      return -1;
    }

    for (int i = 0; i < array.length; ++i) {
      if (array[i] != null && array[i].equals(element)) {
        return i;
      }
    }

    return -1;
  }
}
