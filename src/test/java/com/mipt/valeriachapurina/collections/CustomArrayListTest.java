package com.mipt.valeriachapurina.collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CustomArrayListTest {
  private CustomArrayList<Integer> list;

  @BeforeEach
  void setUp() {
    list = new CustomArrayList<>();
  }

  // --- add() ---

  @Test
  void testAddIncreasesSize() {
    list.add(10);
    list.add(20);
    assertEquals(2, list.size());
  }

  @Test
  void testAddStoresCorrectValue() {
    list.add(5);
    assertEquals(5, list.get(0));
  }

  @Test
  void testAddNullThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> list.add(null));
  }

  // --- get() ---

  @Test
  void testGetReturnsCorrectValue() {
    list.add(100);
    list.add(200);
    assertEquals(200, list.get(1));
  }

  @Test
  void testGetInvalidIndexThrowsException() {
    assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    list.add(10);
    assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
  }

  // --- remove() ---

  @Test
  void testRemoveReturnsRemovedElement() {
    list.add(1);
    list.add(2);
    list.add(3);
    assertEquals(2, list.remove(1));
  }

  @Test
  void testRemoveShiftsElements() {
    list.add(10);
    list.add(20);
    list.add(30);
    list.remove(0);
    assertEquals(20, list.get(0));
    assertEquals(30, list.get(1));
  }

  @Test
  void testRemoveInvalidIndexThrowsException() {
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
  }

  // --- size() ---

  @Test
  void testSize() {
    assertEquals(0, list.size());
    list.add(1);
    list.add(2);
    assertEquals(2, list.size());
  }

  // --- isEmpty() ---

  @Test
  void testIsEmpty() {
    assertTrue(list.isEmpty());
    list.add(7);
    assertFalse(list.isEmpty());
  }

  // --- Конструктор с capacity ---

  @Test
  void testCustomCapacityConstructor() {
    CustomArrayList<String> l = new CustomArrayList<>(5);
    assertEquals(0, l.size());
  }

  @Test
  void testConstructorInvalidCapacityThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> new CustomArrayList<>(0));
    assertThrows(IllegalArgumentException.class, () -> new CustomArrayList<>(-10));
  }

  // --- resize() ---

  @Test
  void testResizeTriggersCorrectly() {
    CustomArrayList<Integer> l = new CustomArrayList<>(2);
    l.add(1);
    l.add(2);
    l.add(3);
    assertEquals(3, l.size());
    assertEquals(3, l.get(2));
  }

  // --- iterator() ---

  @Test
  void testIteratorHasNextAndNext() {
    list.add(1);
    list.add(2);

    Iterator<Integer> it = list.iterator();

    assertTrue(it.hasNext());
    assertEquals(1, it.next());
    assertTrue(it.hasNext());
    assertEquals(2, it.next());
    assertFalse(it.hasNext());
  }

  @Test
  void testIteratorNextThrowsWhenNoMoreElements() {
    list.add(1);

    Iterator<Integer> it = list.iterator();
    it.next();
    assertThrows(NoSuchElementException.class, it::next);
  }

  @Test
  void testIteratorRemoveRemovesLastReturned() {
    list.add(10);
    list.add(20);
    list.add(30);

    Iterator<Integer> it = list.iterator();
    it.next();
    it.remove();

    assertEquals(20, list.get(0));
    assertEquals(2, list.size());
  }

  @Test
  void testIteratorRemoveWithoutNextThrows() {
    Iterator<Integer> it = list.iterator();
    assertThrows(IllegalStateException.class, it::remove);
  }

  @Test
  void testIteratorRemoveTwiceThrows() {
    list.add(1);
    Iterator<Integer> it = list.iterator();
    it.next();
    it.remove();
    assertThrows(IllegalStateException.class, it::remove);
  }
}
