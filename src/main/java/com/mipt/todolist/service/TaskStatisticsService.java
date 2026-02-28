package com.mipt.todolist.service;

import com.mipt.todolist.model.Task;
import com.mipt.todolist.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис статистики по задачам. Демонстрирует использование @Qualifier:
 * инжектирует основной репозиторий (@Primary) и заглушку по имени
 */
@Service
public class TaskStatisticsService {

    @Value("${app.name:To-Do List Manager}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    private final TaskRepository primaryRepository;
    private final TaskRepository stubTaskRepository;

    public TaskStatisticsService(
            TaskRepository primaryRepository,
            @Qualifier("stubTaskRepository") TaskRepository stubTaskRepository) {
        this.primaryRepository = primaryRepository;
        this.stubTaskRepository = stubTaskRepository;
    }

    /**
     * Возвращает сводку: количество задач в основном репозитории и в заглушке
     */
    public Map<String, Object> getRepositoriesComparison() {
        List<Task> primaryTasks = primaryRepository.findAll();
        List<Task> stubTasks = stubTaskRepository.findAll();
        Map<String, Object> result = new HashMap<>();
        result.put("primaryRepositoryCount", primaryTasks.size());
        result.put("stubRepositoryCount", stubTasks.size());
        result.put("source", "primary");
        return result;
    }
}
