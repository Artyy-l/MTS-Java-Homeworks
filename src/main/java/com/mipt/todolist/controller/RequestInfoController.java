package com.mipt.todolist.controller;

import com.mipt.todolist.config.RequestScopedBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Контроллер для демонстрации request-scoped бина: каждый запрос получает свой requestId и время
 */
@RestController
@RequestMapping("/api")
public class RequestInfoController {
    private final RequestScopedBean requestScopedBean;

    public RequestInfoController(RequestScopedBean requestScopedBean) {
        this.requestScopedBean = requestScopedBean;
    }

    /**
     * Возвращает идентификатор запроса и время начала обработки (разные для каждого HTTP-запроса)
     */
    @GetMapping("/request-info")
    public ResponseEntity<Map<String, Object>> getRequestInfo() {
        return ResponseEntity.ok(Map.of(
                "requestId", requestScopedBean.getRequestId(),
                "startTimeMillis", requestScopedBean.getStartTimeMillis()
        ));
    }
}
