package com.mipt.todolist.exception;

/**
 * Задача с указанным идентификатором не найдена
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("Task not found: " + id);
    }
}
