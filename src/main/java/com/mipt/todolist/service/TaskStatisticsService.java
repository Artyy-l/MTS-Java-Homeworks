package com.mipt.todolist.service;

import com.mipt.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TaskStatisticsService {

    @Value("${app.name:To-Do List Manager}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    private final TaskRepository taskRepository;

    public TaskStatisticsService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Map<String, Object> getRepositoriesComparison() {
        Map<String, Object> result = new HashMap<>();
        result.put("primaryRepositoryCount", taskRepository.count());
        result.put("repository", "spring-data-jpa");
        result.put("appName", appName);
        result.put("appVersion", appVersion);
        return result;
    }
}
