package com.mipt.todolist.repository;

import com.mipt.todolist.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация репозитория задач с хранением в памяти
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Task> storage = new ConcurrentHashMap<>();

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(java.util.UUID.randomUUID().toString());
        }
        storage.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}
