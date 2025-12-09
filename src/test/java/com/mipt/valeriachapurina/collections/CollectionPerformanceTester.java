package com.mipt.valeriachapurina.collections;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionPerformanceTester {
  private static final int ELEMENTS = 10000;

  private long measure(Runnable action) {
    long start = System.nanoTime();
    action.run();
    return (System.nanoTime() - start) / 1_000_000;
  }

  private long testAddEnd(List<Integer> list) {
    return measure(() -> {
      for (int i = 0; i < ELEMENTS; ++i) {
        list.add(i);
      }
    });
  }

  private long testAddStart(List<Integer> list) {
    return measure(() -> {
      for (int i = 0; i < ELEMENTS; ++i) {
        list.addFirst(i);
      }
    });
  }

  private long testAddMiddle(List<Integer> list) {
    return measure(() -> {
      for (int i = 0; i < ELEMENTS; ++i) {
        list.add(list.size() / 2, i);
      }
    });
  }

  private long testGetByIndex(List<Integer> list) {
    return measure(() -> {
      for (int i = 0; i < ELEMENTS; ++i) {
        list.get(i);
      }
    });
  }

  private long testRemoveStart(List<Integer> list) {
    return measure(() -> {
      for (int i = 0; i < ELEMENTS; ++i) {
        list.removeFirst();
      }
    });
  }

  private long testRemoveEnd(List<Integer> list) {
    return measure(() -> {
      for (int i = ELEMENTS - 1; i >= 0; i--) {
        list.remove(i);
      }
    });
  }

  @Test
  public void comparePerformance() {
    List<String> operations = List.of(
            "Add end",
            "Add start",
            "Add middle",
            "Get by index",
            "Remove start",
            "Remove end"
    );

    long[] arrayListResults = new long[operations.size()];
    long[] linkedListResults = new long[operations.size()];

    int index = 0;

    List<Integer> arrayList;
    List<Integer> linkedList;

    arrayList = new ArrayList<>();
    linkedList = new LinkedList<>();
    arrayListResults[index] = testAddEnd(arrayList);
    linkedListResults[index++] = testAddEnd(linkedList);

    arrayList = new ArrayList<>();
    linkedList = new LinkedList<>();
    arrayListResults[index] = testAddStart(arrayList);
    linkedListResults[index++] = testAddStart(linkedList);

    arrayList = new ArrayList<>();
    linkedList = new LinkedList<>();
    arrayListResults[index] = testAddMiddle(arrayList);
    linkedListResults[index++] = testAddMiddle(linkedList);

    arrayList = new ArrayList<>();
    linkedList = new LinkedList<>();
    for (int i = 0; i < ELEMENTS; ++i) {
      arrayList.add(i);
      linkedList.add(i);
    }
    arrayListResults[index] = testGetByIndex(arrayList);
    linkedListResults[index++] = testGetByIndex(linkedList);

    arrayList = new ArrayList<>();
    linkedList = new LinkedList<>();
    for (int i = 0; i < ELEMENTS; ++i) {
      arrayList.add(i);
      linkedList.add(i);
    }
    arrayListResults[index] = testRemoveStart(arrayList);
    linkedListResults[index++] = testRemoveStart(linkedList);

    arrayList = new ArrayList<>();
    linkedList = new LinkedList<>();
    for (int i = 0; i < ELEMENTS; ++i) {
      arrayList.add(i);
      linkedList.add(i);
    }
    arrayListResults[index] = testRemoveEnd(arrayList);
    linkedListResults[index] = testRemoveEnd(linkedList);

    System.out.println("\nРезультаты сравнения коллекций (время в ms):");
    System.out.println("---------------------------------------------------------------");
    System.out.printf("%-20s | %-12s | %-12s%n", "Operation", "ArrayList", "LinkedList");
    System.out.println("---------------------------------------------------------------");

    for (int i = 0; i < operations.size(); ++i) {
      System.out.printf("%-20s | %-12d | %-12d%n",
              operations.get(i), arrayListResults[i], linkedListResults[i]);
    }

    System.out.println("---------------------------------------------------------------");
  }
}
