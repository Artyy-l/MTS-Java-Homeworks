package com.mipt.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Главный класс приложения To-Do List Manager
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
public class TodoListApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }
}
