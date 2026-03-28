package com.mipt.todolist.config;

import com.mipt.todolist.validation.CurrentHttpRequestHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Прокидывает текущий {@link HttpServletRequest} в {@link CurrentHttpRequestHolder} для валидаторов
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class CurrentHttpRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            CurrentHttpRequestHolder.set(request);
            filterChain.doFilter(request, response);
        } finally {
            CurrentHttpRequestHolder.clear();
        }
    }
}
