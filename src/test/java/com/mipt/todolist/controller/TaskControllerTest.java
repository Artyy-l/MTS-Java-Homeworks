package com.mipt.todolist.controller;

import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.dto.TaskUpdateDto;
import com.mipt.todolist.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Модульные тесты для TaskController: позитивные и негативные сценарии по каждому endpoint
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/tasks";
    }

    @Nested
    @DisplayName("GET /api/tasks")
    class GetAllTasks {

        @Test
        @DisplayName("позитивный: возвращает 200 и список задач")
        void getAllTasks_positive() {
            ResponseEntity<TaskResponseDto[]> response = restTemplate.getForEntity(baseUrl, TaskResponseDto[].class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getHeaders().getFirst(TaskController.HEADER_TOTAL_COUNT)).isNotNull();
        }

        @Test
        @DisplayName("негативный: запрос к несуществующему пути возвращает 404")
        void getAllTasks_wrongPath_returns404() {
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/api/task", String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("GET /api/tasks/{id}")
    class GetTaskById {

        @Test
        @DisplayName("позитивный: существующая задача возвращает 200 и задачу")
        void getById_positive() {
            TaskResponseDto created = createTask("Title", "Desc", false);
            String id = created.getId();
            ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity(baseUrl + "/" + id, TaskResponseDto.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(id);
            assertThat(response.getBody().getTitle()).isEqualTo("Title");
        }

        @Test
        @DisplayName("негативный: несуществующий id возвращает 404")
        void getById_notFound_returns404() {
            ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/non-existent-id-123", String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("POST /api/tasks")
    class CreateTask {

        @Test
        @DisplayName("позитивный: создание задачи возвращает 201 и задачу с id")
        void createTask_positive() {
            TaskCreateDto dto = validCreateDto("New Task", "New Description");
            ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity(baseUrl, dto, TaskResponseDto.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isNotBlank();
            assertThat(response.getBody().getTitle()).isEqualTo("New Task");
            assertThat(response.getBody().getPriority()).isEqualTo(Priority.MEDIUM);
        }

        @Test
        @DisplayName("негативный: невалидное тело — 400")
        void createTask_invalid_returns400() {
            TaskCreateDto dto = new TaskCreateDto();
            dto.setTitle("ab");
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, dto, String.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{id}")
    class UpdateTask {

        @Test
        @DisplayName("позитивный: обновление существующей задачи возвращает 200")
        void updateTask_positive() {
            TaskResponseDto created = createTask("Original", "Desc", false);
            String id = created.getId();
            TaskUpdateDto update = new TaskUpdateDto();
            update.setTitle("Updated Title");
            update.setDescription("Updated Desc");
            update.setCompleted(true);
            ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    new HttpEntity<>(update),
                    TaskResponseDto.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
            assertThat(response.getBody().isCompleted()).isTrue();
        }

        @Test
        @DisplayName("негативный: обновление несуществующей задачи возвращает 404")
        void updateTask_notFound_returns404() {
            TaskUpdateDto update = new TaskUpdateDto();
            update.setTitle("Some title");
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/non-existent",
                    HttpMethod.PUT,
                    new HttpEntity<>(update),
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("DELETE /api/tasks/{id}")
    class DeleteTask {

        @Test
        @DisplayName("позитивный: удаление существующей задачи возвращает 204")
        void deleteTask_positive() {
            TaskResponseDto created = createTask("To Delete", "Desc", false);
            String id = created.getId();
            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            ResponseEntity<String> getAfter = restTemplate.getForEntity(baseUrl + "/" + id, String.class);
            assertThat(getAfter.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("негативный: удаление несуществующей задачи возвращает 404")
        void deleteTask_notFound_returns404() {
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/non-existent-id",
                    HttpMethod.DELETE,
                    null,
                    String.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    private TaskCreateDto validCreateDto(String title, String description) {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setPriority(Priority.MEDIUM);
        dto.setDueDate(LocalDate.now());
        return dto;
    }

    private TaskResponseDto createTask(String title, String description, boolean completed) {
        TaskCreateDto dto = validCreateDto(title, description);
        ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity(baseUrl, dto, TaskResponseDto.class);
        assertThat(response.getBody()).isNotNull();
        TaskResponseDto body = response.getBody();
        if (completed) {
            TaskUpdateDto u = new TaskUpdateDto();
            u.setCompleted(true);
            ResponseEntity<TaskResponseDto> put = restTemplate.exchange(
                    baseUrl + "/" + body.getId(),
                    HttpMethod.PUT,
                    new HttpEntity<>(u),
                    TaskResponseDto.class
            );
            assertThat(put.getBody()).isNotNull();
            return put.getBody();
        }
        return body;
    }
}
