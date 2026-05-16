package com.mipt.todolist.external;

import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.dto.TaskUpdateDto;
import com.mipt.todolist.model.Priority;
import com.mipt.todolist.validation.OnCreate;
import com.mipt.todolist.validation.OnUpdate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<String, TaskResponseDto> tasks = new ConcurrentHashMap<>();

    public ExternalApiController() {
        TaskResponseDto task = new TaskResponseDto();
        task.setId("demo-task");
        task.setTitle("External demo task");
        task.setDescription("Seed task from external emulator");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setPriority(Priority.MEDIUM);
        tasks.put(task.getId(), task);
    }

    @PostMapping(value = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskResponseDto> createTask(@Validated(OnCreate.class) @RequestBody TaskCreateDto request) {
        TaskResponseDto task = new TaskResponseDto();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
        task.setTags(request.getTags());
        tasks.put(task.getId(), task);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();
        return ResponseEntity.created(location).body(task);
    }

    @GetMapping(value = "/tasks/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTask(@PathVariable("id") String id) {
        TaskResponseDto task = tasks.get(id);
        if (task == null) {
            return notFound(id);
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskResponseDto> getTasks(@RequestParam(value = "completed", required = false) Boolean completed,
                                          @RequestParam(value = "limit", required = false) Integer limit) {
        return tasks.values().stream()
                .filter(task -> completed == null || task.isCompleted() == completed)
                .sorted(Comparator.comparing(TaskResponseDto::getCreatedAt))
                .limit(limit == null || limit < 0 ? Long.MAX_VALUE : limit)
                .toList();
    }

    @PutMapping(value = "/tasks/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTask(@PathVariable("id") String id,
                                        @Validated(OnUpdate.class) @RequestBody TaskUpdateDto request) {
        TaskResponseDto task = tasks.get(id);
        if (task == null) {
            return notFound(id);
        }
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable("id") String id) {
        TaskResponseDto removed = tasks.remove(id);
        if (removed == null) {
            return notFound(id);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<String> unstable(@RequestParam("mode") String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(3_500);
                yield ResponseEntity.ok("ok");
            }
            case "500" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"external service error\"}");
            case "429" -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header(HttpHeaders.RETRY_AFTER, "5")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"rate limit exceeded\"}");
            case "html" -> ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body><h1>Bad Gateway</h1></body></html>");
            default -> ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"message\":\"unsupported unstable mode\"}");
        };
    }

    private ResponseEntity<?> notFound(String id) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Task not found: " + id);
        problemDetail.setTitle("Task not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }
}
