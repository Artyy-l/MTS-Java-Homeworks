package com.mipt.valeriachapurina.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Реализация пользовательского списка на основе массива
 * @param <A> тип элементов, хранимых в списке
 */
public class CustomArrayList<A> implements CustomList<A> {
  private Object[] elements;
  private int size = 0;

  /**
   * Конструктор создаёт пустой список с начальной ёмкостью 10
   */
  public CustomArrayList() {
    elements = new Object[10];
  }

  /**
   * Конструктор создаёт пустой список с начальной ёмкостью capacity
   * @param capacity ёмкость списка
   * @throws IllegalArgumentException если {@code capacity} <= 0
   */
  public CustomArrayList(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("`capacity` must be a positive integer.");
    }

    elements = new Object[capacity];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void add(A element) {
    if (element == null) {
      throw new IllegalArgumentException("Null values are not allowed.");
    }

    if (size == elements.length) {
      resize();
    }
    elements[size++] = element;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A get(int index) {
    validateIndex(index);
    return (A) elements[index];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A remove(int index) {
    validateIndex(index);
    A removedElement = (A) elements[index];
    shiftLeft(index);
    size--;
    return removedElement;
  }

  /**
   * Увеличивает размер внутреннего массива в 1.5 раза
   */
  private void resize() {
    Object[] newElements = new Object[(int) (elements.length * 1.5)];
    System.arraycopy(elements, 0, newElements, 0, elements.length);
    elements = newElements;
  }

  /**
   * Проверяет, находится ли индекс в пределах допустимого диапазона
   * @param index индекс для проверки
   * @throws IndexOutOfBoundsException если индекс выходит за пределы размера списка
   */
  private void validateIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index out of range");
    }
  }

  /**
   * Сдвигает элементы массива влево, начиная с указанного индекса
   * @param startIndex индекс, с которого начинается сдвиг
   */
  private void shiftLeft(int startIndex) {
    for (int i = startIndex; i < size - 1; ++i) {
      elements[i] = elements[i + 1];
    }
  }

  /**
   * {@inheritDoc}
   */
  public int size() {
    return size;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isEmpty() {
    return size == 0;
  }

  public Iterator<A> iterator() {
    return new CustomArrayListIterator();
  }

  private class CustomArrayListIterator implements Iterator<A> {
    private int currentIndex = -1;
    private boolean canRemove = false;

    /**
     * Returns {@code true} if the iteration has more elements. (In other words, returns
     * {@code true} if {@link #next} would return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
      return currentIndex < size - 1;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public A next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      canRemove = true;
      return (A) elements[++currentIndex];
    }

    /**
     * Removes from the underlying list the last element returned by this iterator. This method can
     * be called only once per call to {@link #next}
     *
     * @throws IllegalStateException if the {@code next} method has not yet been called, or the
     *                               {@code remove} method has already been called after the last
     *                               call to the {@code next} method
     */
    @Override
    public void remove() {
      if (!canRemove) {
        throw new IllegalStateException();
      }

      CustomArrayList.this.remove(currentIndex--);
      canRemove = false;
    }
  }
}


