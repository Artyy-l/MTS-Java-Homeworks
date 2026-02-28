package com.mipt.todolist.config;

import com.mipt.todolist.repository.StubTaskRepository;
import com.mipt.todolist.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация бинов репозиториев
 */
@Configuration
public class RepositoryConfig {

    /**
     * Создаёт бин-заглушку репозитория с фиксированными данными
     */
    @Bean(name = "stubTaskRepository")
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }
}
