package com.mipt.todolist.controller;

import com.mipt.todolist.config.RequestScopedBean;
import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.dto.TaskUpdateDto;
import com.mipt.todolist.mapper.TaskMapper;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.service.TaskService;
import com.mipt.todolist.validation.OnCreate;
import com.mipt.todolist.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST-контроллер для CRUD-операций над задачами и сведений о запросе (request-scoped бин)
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Tasks", description = "Операции с задачами")
public class TaskController {

    public static final String HEADER_TOTAL_COUNT = "X-Total-Count";

    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final RequestScopedBean requestScopedBean;

    public TaskController(TaskService taskService, TaskMapper taskMapper, RequestScopedBean requestScopedBean) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.requestScopedBean = requestScopedBean;
    }

    @Operation(summary = "Список задач")
    @ApiResponse(responseCode = "200", description = "Список задач")
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<Task> tasks = taskService.findAll();
        List<TaskResponseDto> body = tasks.stream().map(taskMapper::toResponseDto).toList();
        return ResponseEntity.ok()
                .header(HEADER_TOTAL_COUNT, String.valueOf(tasks.size()))
                .body(body);
    }

    @Operation(summary = "Задача по id")
    @ApiResponse(responseCode = "200", description = "Найдена")
    @ApiResponse(responseCode = "404", description = "Не найдена")
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @Parameter(description = "Идентификатор задачи") @PathVariable("id") String id) {
        Task task = taskService.findByIdOrThrow(id);
        return ResponseEntity.ok(taskMapper.toResponseDto(task));
    }

    @Operation(summary = "Создать задачу")
    @ApiResponse(responseCode = "201", description = "Создана")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDto> createTask(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
        Task entity = taskMapper.toEntity(dto);
        Task saved = taskService.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toResponseDto(saved));
    }

    @Operation(summary = "Обновить задачу")
    @ApiResponse(responseCode = "200", description = "Обновлена")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @ApiResponse(responseCode = "404", description = "Не найдена")
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @Parameter(description = "Идентификатор задачи") @PathVariable("id") String id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
        Task task = taskService.findByIdOrThrow(id);
        taskMapper.updateEntity(dto, task);
        task.setId(id);
        Task updated = taskService.save(task);
        return ResponseEntity.ok(taskMapper.toResponseDto(updated));
    }

    @Operation(summary = "Удалить задачу")
    @ApiResponse(responseCode = "204", description = "Удалена")
    @ApiResponse(responseCode = "404", description = "Не найдена")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") String id) {
        taskService.findByIdOrThrow(id);
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Идентификатор запроса и время начала обработки (request-scoped бин)
     */
    @Operation(summary = "Сведения о текущем HTTP-запросе")
    @GetMapping("/request-info")
    public ResponseEntity<Map<String, Object>> getRequestInfo() {
        return ResponseEntity.ok(Map.of(
                "requestId", requestScopedBean.getRequestId(),
                "startTimeMillis", requestScopedBean.getStartTimeMillis()
        ));
    }
}
