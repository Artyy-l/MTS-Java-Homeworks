package com.mipt.todolist.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Приоритет задачи
 */
@Schema(description = "Приоритет задачи")
public enum Priority {
    LOW,
    MEDIUM,
    HIGH
}
