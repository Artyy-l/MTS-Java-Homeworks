package com.mipt.valeriachapurina.collections;

import java.util.*;

public class StudentUtils {

  /**
   * возвращает список студентов с оценкой в заданном диапазоне
   *
   * @param map
   * @param minGrade
   * @param maxGrade
   */
  public List<Student> findStudentsByGradeRange(Map<Integer, Student> map, double minGrade, double maxGrade) {
    List<Student> res = new ArrayList<>();
    for (Student student : map.values()) {
      if (student.getGrade() >= minGrade && student.getGrade() <= maxGrade) {
        res.add(student);
      }
    }

    return res;
  }

  /**
   * возвращает n студентов с наибольшими id
   *
   * @param map
   * @param n
   */
  public List<Student> getTopNStudents(TreeMap<Integer, Student> map, int n) {
    List<Student> res = new ArrayList<>();
    for (Student student : map.values()) {
      if (res.size() >= n) {
        break;
      }
      res.add(student);
    }

    return res;
  }

  public static void main(String[] args) {
    HashMap<Integer, Student> studentsMap = new HashMap<>();
    TreeMap<Integer, Student> studentsTreeMap = new TreeMap<>(Collections.reverseOrder());
  }
}
