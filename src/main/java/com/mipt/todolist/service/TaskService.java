package com.mipt.todolist.service;

import com.mipt.todolist.config.PrototypeScopedBean;
import com.mipt.todolist.exception.BulkTaskCompletionException;
import com.mipt.todolist.exception.TaskNotFoundException;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

    @PostConstruct
    public void initCache() {
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(task -> taskCache.put(task.getId(), task));
        log.info("Task cache initialized, loaded {} tasks", taskCache.size());
    }

    @PreDestroy
    public void cleanup() {
        int size = taskCache.size();
        log.info("TaskService shutdown, cache contains {} tasks", size);
        try {
            Path statsFile = Path.of("task-stats.txt");
            String line = "Tasks in cache at shutdown: " + size + System.lineSeparator();
            Files.writeString(statsFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.warn("Could not write task statistics file", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Task> findAll() {
        List<Task> all = taskRepository.findAll();
        all.forEach(task -> taskCache.put(task.getId(), task));
        return all;
    }

    @Transactional(readOnly = true)
    public Optional<Task> findById(String id) {
        Optional<Task> fromRepo = taskRepository.findById(id);
        fromRepo.ifPresent(task -> taskCache.put(task.getId(), task));
        return fromRepo;
    }

    public Task findByIdOrThrow(String id) {
        return findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = RuntimeException.class
    )
    public Task save(Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(idGeneratorProvider.getObject().generateTaskId());
        }
        Task saved = taskRepository.save(task);
        taskCache.put(saved.getId(), saved);
        return saved;
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = RuntimeException.class
    )
    public void deleteById(String id) {
        taskRepository.deleteById(id);
        taskCache.remove(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        return taskRepository.existsById(id);
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = BulkTaskCompletionException.class
    )
    public void bulkCompleteTasks(List<Long> ids) {
        bulkCompleteStringIds(ids.stream().map(String::valueOf).toList());
    }

    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = BulkTaskCompletionException.class
    )
    public void bulkCompleteTasks(Collection<String> ids) {
        bulkCompleteStringIds(ids);
    }

    private void bulkCompleteStringIds(Collection<String> ids) {
        List<Task> tasks = taskRepository.findAllById(ids);
        if (tasks.size() != ids.size()) {
            List<String> foundIds = tasks.stream().map(Task::getId).toList();
            String missingId = ids.stream()
                    .filter(id -> !foundIds.contains(id))
                    .findFirst()
                    .orElse("unknown");
            throw new BulkTaskCompletionException(missingId);
        }
        tasks.forEach(task -> task.setCompleted(true));
        taskRepository.saveAll(tasks);
        tasks.forEach(task -> taskCache.put(task.getId(), task));
    }

    @Transactional(readOnly = true)
    public List<Task> findTasksDueWithinNextSevenDays() {
        LocalDate today = LocalDate.now();
        return taskRepository.findTasksDueWithinNextSevenDays(today, today.plusDays(7));
    }

    @Transactional(readOnly = true)
    public List<Task> findAllWithAttachments() {
        return taskRepository.findAllWithAttachments();
    }
}
