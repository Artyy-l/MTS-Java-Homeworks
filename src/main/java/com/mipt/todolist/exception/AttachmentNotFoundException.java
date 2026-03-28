package com.mipt.todolist.exception;

/**
 * Вложение с указанным идентификатором не найдено
 */
public class AttachmentNotFoundException extends RuntimeException {

    public AttachmentNotFoundException(Long id) {
        super("Attachment not found: " + id);
    }
}
