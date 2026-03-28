package com.mipt.todolist.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Добавляет заголовок {@code X-API-Version} ко всем ответам
 */
@Component
@Order(Integer.MAX_VALUE - 10)
public class ApiVersionResponseFilter extends OncePerRequestFilter {

    public static final String HEADER_API_VERSION = "X-API-Version";

    @Value("${app.api.version:2.0.0}")
    private String apiVersion;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        response.setHeader(HEADER_API_VERSION, apiVersion);
        filterChain.doFilter(request, response);
    }
}
