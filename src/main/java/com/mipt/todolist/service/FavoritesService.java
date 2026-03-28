package com.mipt.todolist.service;

import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.exception.TaskNotFoundException;
import com.mipt.todolist.mapper.TaskMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoritesService {

    public static final String SESSION_ATTR_FAVORITES = "favoriteTaskIds";

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public FavoritesService(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @SuppressWarnings("unchecked")
    public void addFavorite(HttpSession session, String taskId) {
        if (!taskService.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        Set<String> ids = (Set<String>) session.getAttribute(SESSION_ATTR_FAVORITES);
        if (ids == null) {
            ids = new LinkedHashSet<>();
            session.setAttribute(SESSION_ATTR_FAVORITES, ids);
        }
        ids.add(taskId);
    }

    @SuppressWarnings("unchecked")
    public void removeFavorite(HttpSession session, String taskId) {
        Set<String> ids = (Set<String>) session.getAttribute(SESSION_ATTR_FAVORITES);
        if (ids != null) {
            ids.remove(taskId);
        }
    }

    @SuppressWarnings("unchecked")
    public List<TaskResponseDto> getFavoriteTasks(HttpSession session) {
        Set<String> ids = (Set<String>) session.getAttribute(SESSION_ATTR_FAVORITES);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<TaskResponseDto> result = new ArrayList<>();
        for (String id : ids) {
            taskService.findById(id).map(taskMapper::toResponseDto).ifPresent(result::add);
        }
        return result;
    }
}
