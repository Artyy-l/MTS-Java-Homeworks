package com.mipt.todolist.validation;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Держит текущий запрос в потоке для валидаторов (устанавливается фильтром)
 */
public final class CurrentHttpRequestHolder {

    private static final ThreadLocal<HttpServletRequest> REQUEST = new ThreadLocal<>();

    private CurrentHttpRequestHolder() {
    }

    public static void set(HttpServletRequest request) {
        REQUEST.set(request);
    }

    public static HttpServletRequest get() {
        return REQUEST.get();
    }

    public static void clear() {
        REQUEST.remove();
    }
}
