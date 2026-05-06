package com.mipt.todolist.config;

import com.mipt.todolist.repository.TaskRepository;
import com.mipt.todolist.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            log.debug("Bean before initialization: name={}, type={}", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            log.debug("Bean after initialization: name={}, type={}", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }
}
