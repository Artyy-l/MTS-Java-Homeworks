package com.mipt.todolist.controller;

import com.mipt.todolist.model.Task;
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
            ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
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
            Task created = createTask("Title", "Desc", false);
            String id = created.getId();
            ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/" + id, Task.class);
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
            Task task = new Task();
            task.setTitle("New Task");
            task.setDescription("New Description");
            task.setCompleted(false);
            ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, task, Task.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isNotBlank();
            assertThat(response.getBody().getTitle()).isEqualTo("New Task");
        }

        @Test
        @DisplayName("негативный: пустое тело запроса всё равно обрабатывается (тело без title)")
        void createTask_emptyBody() {
            Task task = new Task();
            ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, task, Task.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{id}")
    class UpdateTask {

        @Test
        @DisplayName("позитивный: обновление существующей задачи возвращает 200")
        void updateTask_positive() {
            Task created = createTask("Original", "Desc", false);
            String id = created.getId();
            Task update = new Task(id, "Updated Title", "Updated Desc", true);
            ResponseEntity<Task> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    new HttpEntity<>(update),
                    Task.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
            assertThat(response.getBody().isCompleted()).isTrue();
        }

        @Test
        @DisplayName("негативный: обновление несуществующей задачи возвращает 404")
        void updateTask_notFound_returns404() {
            Task update = new Task("non-existent", "Title", "Desc", false);
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
            Task created = createTask("To Delete", "Desc", false);
            String id = created.getId();
            ResponseEntity<Void> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            ResponseEntity<Task> getAfter = restTemplate.getForEntity(baseUrl + "/" + id, Task.class);
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

    private Task createTask(String title, String description, boolean completed) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, task, Task.class);
        assertThat(response.getBody()).isNotNull();
        return response.getBody();
    }
}
