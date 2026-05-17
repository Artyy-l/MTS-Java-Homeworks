package com.mipt.todolist.repository;

import com.mipt.todolist.model.Priority;
import com.mipt.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("todolist_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRES::getDriverClassName);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findTasksDueWithinNextSevenDays_usesPostgresqlContainer() {
        LocalDate today = LocalDate.of(2026, 5, 16);
        Task soon = task("soon", today.plusDays(3));
        Task todayTask = task("today", today);
        Task later = task("later", today.plusDays(10));
        taskRepository.saveAll(List.of(soon, todayTask, later));

        List<Task> result = taskRepository.findTasksDueWithinNextSevenDays(today, today.plusDays(7));

        assertThat(result)
                .extracting(Task::getId)
                .containsExactlyInAnyOrder(soon.getId(), todayTask.getId());
    }

    private Task task(String title, LocalDate dueDate) {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(title);
        task.setDescription("description");
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.of(2026, 5, 16, 12, 0));
        task.setDueDate(dueDate);
        task.setPriority(Priority.MEDIUM);
        task.setTags(Set.of("test"));
        return task;
    }
}
