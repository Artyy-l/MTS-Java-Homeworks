package com.mipt.todolist.repository;

import com.mipt.todolist.model.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    List<TaskAttachment> findByTask_Id(String taskId);
}
