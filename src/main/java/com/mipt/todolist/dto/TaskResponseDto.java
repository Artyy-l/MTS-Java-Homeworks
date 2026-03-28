package com.mipt.todolist.dto;

import com.mipt.todolist.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "Задача в ответе API")
public class TaskResponseDto {

    @Schema(example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(example = "Buy milk")
    private String title;

    @Schema(example = "2% at store")
    private String description;

    @Schema(example = "false")
    private boolean completed;

    @Schema(example = "2026-03-28T10:00:00")
    private LocalDateTime createdAt;

    @Schema(example = "2026-12-31")
    private LocalDate dueDate;

    @Schema(example = "MEDIUM")
    private Priority priority;

    @Schema
    private Set<String> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
