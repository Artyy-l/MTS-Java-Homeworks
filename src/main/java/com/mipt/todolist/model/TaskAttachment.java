package com.mipt.todolist.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Метаданные вложения к задаче
 */
@Entity
@Table(name = "task_attachments")
public class TaskAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false, insertable = false, updatable = false, length = 36)
    private String taskId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long size;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    public TaskAttachment() {
    }

    public TaskAttachment(Long id, String taskId, String fileName, String storedFileName,
                          String contentType, long size, LocalDateTime uploadedAt) {
        this.id = id;
        this.taskId = taskId;
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        if (task != null) {
            return task.getId();
        }
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
        this.taskId = task != null ? task.getId() : null;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskAttachment that = (TaskAttachment) o;
        return size == that.size
                && Objects.equals(id, that.id)
                && Objects.equals(taskId, that.taskId)
                && Objects.equals(fileName, that.fileName)
                && Objects.equals(storedFileName, that.storedFileName)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(uploadedAt, that.uploadedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskId, fileName, storedFileName, contentType, size, uploadedAt);
    }
}
