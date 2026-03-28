package com.mipt.todolist.controller;

import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Избранные задачи (сессия)")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @Operation(summary = "Добавить задачу в избранное")
    @ApiResponse(responseCode = "200", description = "Добавлено")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    @PostMapping("/{taskId}")
    public ResponseEntity<Void> add(
            @Parameter(description = "Идентификатор задачи") @PathVariable("taskId") String taskId,
            HttpSession session) {
        favoritesService.addFavorite(session, taskId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Убрать из избранного")
    @ApiResponse(responseCode = "204", description = "Удалено из избранного")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> remove(@PathVariable("taskId") String taskId, HttpSession session) {
        favoritesService.removeFavorite(session, taskId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Список избранных задач")
    @ApiResponse(responseCode = "200", description = "Список")
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> list(HttpSession session) {
        return ResponseEntity.ok(favoritesService.getFavoriteTasks(session));
    }
}
