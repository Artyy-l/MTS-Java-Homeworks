package com.mipt.todolist.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Бин с областью видимости prototype: при каждом обращении создаётся новый экземпляр.
 * Генерирует уникальные идентификаторы для задач на основе UUID
 */
@Component
@Scope("prototype")
public class PrototypeScopedBean {

    /**
     * Возвращает новый уникальный идентификатор для задачи
     */
    public String generateTaskId() {
        return UUID.randomUUID().toString();
    }
}
