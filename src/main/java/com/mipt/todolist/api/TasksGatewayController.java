package com.mipt.todolist.api;

import com.mipt.todolist.client.ExternalTasksClient;
import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.service.TasksGatewayService;
import com.mipt.todolist.validation.OnCreate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {

    private final TasksGatewayService tasksGatewayService;

    public TasksGatewayController(TasksGatewayService tasksGatewayService) {
        this.tasksGatewayService = tasksGatewayService;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Validated(OnCreate.class) @RequestBody TaskCreateDto request) {
        ExternalTasksClient.CreatedTask createdTask = tasksGatewayService.createTask(request);
        if (createdTask.location() == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(createdTask.task());
        }
        return ResponseEntity.created(createdTask.location()).body(createdTask.task());
    }

    @GetMapping("/{id}")
    public TaskResponseDto getTask(@PathVariable("id") String id) {
        return tasksGatewayService.getTask(id);
    }

    @GetMapping
    public List<TaskResponseDto> getTasks(@RequestParam(value = "completed", required = false) Boolean completed,
                                          @RequestParam(value = "limit", required = false) Integer limit) {
        return tasksGatewayService.getTasks(completed, limit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") String id) {
        tasksGatewayService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public String unstable(@RequestParam("mode") String mode) {
        return tasksGatewayService.callUnstable(mode);
    }
}
