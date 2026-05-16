package com.mipt.todolist.service;

import com.mipt.todolist.client.ExternalTasksClient;
import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.exception.TaskNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TasksGatewayService {

    private final ExternalTasksClient externalTasksClient;

    public TasksGatewayService(ExternalTasksClient externalTasksClient) {
        this.externalTasksClient = externalTasksClient;
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public ExternalTasksClient.CreatedTask createTask(TaskCreateDto request) {
        return externalTasksClient.createTask(request);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public TaskResponseDto getTask(String id) {
        return externalTasksClient.getTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTasksFallback")
    public List<TaskResponseDto> getTasks(Boolean completed, Integer limit) {
        return externalTasksClient.getTasks(completed, limit);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public void deleteTask(String id) {
        externalTasksClient.deleteTask(id);
    }

    @RateLimiter(name = "externalApi")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "unstableFallback")
    public String callUnstable(String mode) {
        return externalTasksClient.callUnstable(mode);
    }

    public ExternalTasksClient.CreatedTask createTaskFallback(TaskCreateDto request, Throwable throwable) {
        rethrowNotFound(throwable);
        TaskResponseDto task = fallbackTask("fallback-create", request.getTitle(), "Task was not created in external API");
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setTags(request.getTags());
        return new ExternalTasksClient.CreatedTask(task, null);
    }

    public TaskResponseDto getTaskFallback(String id, Throwable throwable) {
        rethrowNotFound(throwable);
        return fallbackTask(id, "External task unavailable", "Fallback response because external API is unavailable");
    }

    public List<TaskResponseDto> getTasksFallback(Boolean completed, Integer limit, Throwable throwable) {
        rethrowNotFound(throwable);
        TaskResponseDto task = fallbackTask("fallback-list", "External tasks unavailable",
                "Fallback list because external API is unavailable");
        task.setCompleted(completed != null && completed);
        return List.of(task);
    }

    public void deleteTaskFallback(String id, Throwable throwable) {
        rethrowNotFound(throwable);
    }

    public String unstableFallback(String mode, Throwable throwable) {
        return "fallback for unstable mode: " + mode;
    }

    private TaskResponseDto fallbackTask(String id, String title, String description) {
        TaskResponseDto task = new TaskResponseDto();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    private void rethrowNotFound(Throwable throwable) {
        if (throwable instanceof TaskNotFoundException taskNotFoundException) {
            throw taskNotFoundException;
        }
    }
}
