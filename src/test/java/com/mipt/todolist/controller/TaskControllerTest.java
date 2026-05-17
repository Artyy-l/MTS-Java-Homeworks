package com.mipt.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.todolist.config.RequestScopedBean;
import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.exception.GlobalExceptionHandler;
import com.mipt.todolist.mapper.TaskMapper;
import com.mipt.todolist.model.Priority;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {TaskController.class, GlobalExceptionHandler.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskMapper taskMapper;

    @MockitoBean
    private RequestScopedBean requestScopedBean;

    @Test
    void createTask_returnsCreatedTask() throws Exception {
        TaskCreateDto request = createDto();
        Task entity = task(null, request.getTitle(), request.getDescription(), false);
        Task saved = task("task-1", request.getTitle(), request.getDescription(), false);
        TaskResponseDto response = responseDto(saved);

        when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(entity);
        when(taskService.save(entity)).thenReturn(saved);
        when(taskMapper.toResponseDto(saved)).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("task-1"))
                .andExpect(jsonPath("$.title").value("Write tests"))
                .andExpect(jsonPath("$.description").value("Cover controller"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.tags[0]").value("homework"));

        verify(taskMapper).toEntity(any(TaskCreateDto.class));
        verify(taskService).save(entity);
        verify(taskMapper).toResponseDto(saved);
    }

    @Test
    void getTaskById_returnsExistingTask() throws Exception {
        Task task = task("task-2", "Read docs", "Check MockMvc", true);
        TaskResponseDto response = responseDto(task);

        when(taskService.findByIdOrThrow("task-2")).thenReturn(task);
        when(taskMapper.toResponseDto(task)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/{id}", "task-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-2"))
                .andExpect(jsonPath("$.title").value("Read docs"))
                .andExpect(jsonPath("$.description").value("Check MockMvc"))
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.tags[0]").value("homework"));

        verify(taskService).findByIdOrThrow("task-2");
        verify(taskMapper).toResponseDto(task);
    }

    @Test
    void createTask_withInvalidRequest_returnsBadRequest() throws Exception {
        TaskCreateDto request = createDto();
        request.setTitle("ab");

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    private TaskCreateDto createDto() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Write tests");
        dto.setDescription("Cover controller");
        dto.setDueDate(LocalDate.now().plusDays(1));
        dto.setPriority(Priority.MEDIUM);
        dto.setTags(Set.of("homework"));
        return dto;
    }

    private Task task(String id, String title, String description, boolean completed) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        task.setCreatedAt(LocalDateTime.of(2026, 5, 16, 12, 0));
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("homework"));
        return task;
    }

    private TaskResponseDto responseDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setCompleted(task.isCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setPriority(task.getPriority());
        dto.setTags(task.getTags());
        return dto;
    }
}
