package com.mipt.todolist.repository;

import com.mipt.todolist.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Заглушка репозитория с фиксированным набором задач для тестирования
 */
public class StubTaskRepository implements TaskRepository {
    private static final List<Task> STUB_TASKS = List.of(
            new Task("stub-1", "Stub Task 1", "Description 1", false),
            new Task("stub-2", "Stub Task 2", "Description 2", true)
    );

    @Override
    public List<Task> findAll() {
        return List.copyOf(STUB_TASKS);
    }

    @Override
    public Optional<Task> findById(String id) {
        return STUB_TASKS.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public Task save(Task task) {
        return task;
    }

    @Override
    public void deleteById(String id) {
    }

    @Override
    public boolean existsById(String id) {
        return STUB_TASKS.stream().anyMatch(t -> t.getId().equals(id));
    }
}
