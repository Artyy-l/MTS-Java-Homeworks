package com.mipt.todolist.repository;

import com.mipt.todolist.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для CRUD-операций над задачами
 */
public interface TaskRepository {

    /**
     * Возвращает все задачи
     */
    List<Task> findAll();

    /**
     * Находит задачу по идентификатору
     */
    Optional<Task> findById(String id);

    /**
     * Сохраняет задачу (создание или обновление)
     */
    Task save(Task task);

    /**
     * Удаляет задачу по идентификатору
     */
    void deleteById(String id);

    /**
     * Проверяет наличие задачи по идентификатору
     */
    boolean existsById(String id);
}
