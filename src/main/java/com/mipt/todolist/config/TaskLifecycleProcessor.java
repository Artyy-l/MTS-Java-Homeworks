package com.mipt.todolist.config;

import com.mipt.todolist.repository.TaskRepository;
import com.mipt.todolist.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Обработчик жизненного цикла бинов. Логирует создание и инициализацию бинов TaskService и TaskRepository
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof TaskService) {
            log.info("TaskLifecycleProcessor: до инициализации — бин TaskService '{}'", beanName);
        } else if (bean instanceof TaskRepository) {
            log.info("TaskLifecycleProcessor: до инициализации — бин TaskRepository '{}'", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof TaskService) {
            log.info("TaskLifecycleProcessor: после инициализации — бин TaskService '{}'", beanName);
        } else if (bean instanceof TaskRepository) {
            log.info("TaskLifecycleProcessor: после инициализации — бин TaskRepository '{}'", beanName);
        }
        return bean;
    }
}
