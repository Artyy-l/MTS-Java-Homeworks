package com.mipt.todolist.repository;

import com.mipt.todolist.model.TaskAttachment;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий метаданных вложений
 */
public interface TaskAttachmentRepository {

    TaskAttachment save(TaskAttachment attachment);

    Optional<TaskAttachment> findById(Long id);

    List<TaskAttachment> findByTaskId(String taskId);

    void deleteById(Long id);
}
