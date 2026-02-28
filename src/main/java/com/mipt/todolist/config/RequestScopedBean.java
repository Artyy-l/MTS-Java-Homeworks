package com.mipt.todolist.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

/**
 * Бин с областью видимости request: для каждого HTTP-запроса создаётся новый экземпляр.
 * Хранит requestId и время начала обработки запроса
 */
@Component
@RequestScope
public class RequestScopedBean {
    private final String requestId = UUID.randomUUID().toString();
    private final long startTimeMillis = System.currentTimeMillis();

    public String getRequestId() {
        return requestId;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }
}
