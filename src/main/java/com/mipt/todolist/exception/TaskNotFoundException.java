package com.mipt.todolist.exception;

/**
 * Задача с указанным идентификатором не найдена
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("Task not found: " + id);
    }

    public static TaskNotFoundException withMessage(String message) {
        return new TaskNotFoundException(message, true);
    }

    private TaskNotFoundException(String message, boolean rawMessage) {
        super(message);
    }
}
