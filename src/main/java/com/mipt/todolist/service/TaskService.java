package com.mipt.todolist.service;

import com.mipt.todolist.config.PrototypeScopedBean;
import com.mipt.todolist.exception.TaskNotFoundException;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис управления задачами. Использует кэш задач, инициализируемый при старте
 */
@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final ObjectProvider<PrototypeScopedBean> idGeneratorProvider;
    private final Map<String, Task> taskCache = new ConcurrentHashMap<>();

    public TaskService(TaskRepository taskRepository,
                       ObjectProvider<PrototypeScopedBean> idGeneratorProvider) {
        this.taskRepository = taskRepository;
        this.idGeneratorProvider = idGeneratorProvider;
    }

    /**
     * Инициализация кэша: загрузка предопределённых задач из репозитория при старте приложения
     */
    @PostConstruct
    public void initCache() {
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(t -> taskCache.put(t.getId(), t));
        log.info("TaskService: кэш инициализирован, загружено {} задач", taskCache.size());
    }

    /**
     * Очистка ресурсов: логирование размера кэша и сохранение статистики в файл
     */
    @PreDestroy
    public void cleanup() {
        int size = taskCache.size();
        log.info("TaskService: завершение работы, в кэше было {} задач", size);
        try {
            Path statsFile = Path.of("task-stats.txt");
            String line = "Задач в кэше при остановке: " + size + System.lineSeparator();
            Files.writeString(statsFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.warn("TaskService: не удалось записать файл статистики", e);
        }
    }

    public List<Task> findAll() {
        List<Task> all = taskRepository.findAll();
        all.forEach(t -> taskCache.put(t.getId(), t));
        return all;
    }

    public Optional<Task> findById(String id) {
        Optional<Task> fromRepo = taskRepository.findById(id);
        fromRepo.ifPresent(t -> taskCache.put(t.getId(), t));
        return fromRepo;
    }

    public Task findByIdOrThrow(String id) {
        return findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task save(Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(idGeneratorProvider.getObject().generateTaskId());
        }
        Task saved = taskRepository.save(task);
        taskCache.put(saved.getId(), saved);
        return saved;
    }

    public void deleteById(String id) {
        taskRepository.deleteById(id);
        taskCache.remove(id);
    }

    public boolean existsById(String id) {
        return taskRepository.existsById(id);
    }
}
