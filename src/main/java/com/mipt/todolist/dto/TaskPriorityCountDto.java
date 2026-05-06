package com.mipt.todolist.dto;

import com.mipt.todolist.model.Priority;

public record TaskPriorityCountDto(Priority priority, long count) {
}
