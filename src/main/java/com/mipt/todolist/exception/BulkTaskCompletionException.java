package com.mipt.todolist.exception;

public class BulkTaskCompletionException extends RuntimeException {

    public BulkTaskCompletionException(String id) {
        super("Task not found during bulk completion: " + id);
    }
}
