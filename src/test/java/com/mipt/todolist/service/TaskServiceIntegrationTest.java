package com.mipt.todolist.service;

import com.mipt.todolist.exception.BulkTaskCompletionException;
import com.mipt.todolist.model.Priority;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.repository.TaskAttachmentRepository;
import com.mipt.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TaskStatisticsJdbcService taskStatisticsJdbcService;

    @BeforeEach
    void cleanDatabase() {
        attachmentRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    void bulkCompleteTasks_rollsBackWhenAnyTaskIsMissing() {
        Task first = taskService.save(task("first"));
        Task second = taskService.save(task("second"));

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(first.getId(), "missing-id", second.getId())))
                .isInstanceOf(BulkTaskCompletionException.class);

        assertThat(taskRepository.findById(first.getId()).orElseThrow().isCompleted()).isFalse();
        assertThat(taskRepository.findById(second.getId()).orElseThrow().isCompleted()).isFalse();
    }

    @Test
    void bulkCompleteTasks_marksAllTasksCompletedWhenIdsExist() {
        Task first = taskService.save(task("first"));
        Task second = taskService.save(task("second"));

        taskService.bulkCompleteTasks(List.of(first.getId(), second.getId()));

        assertThat(taskRepository.findAllById(List.of(first.getId(), second.getId())))
                .extracting(Task::isCompleted)
                .containsOnly(true);
    }

    @Test
    void getTasksCountByPriority_usesJdbcTemplateRowMapper() {
        taskService.save(task("first", Priority.HIGH));
        taskService.save(task("second", Priority.HIGH));
        taskService.save(task("third", Priority.LOW));

        assertThat(taskStatisticsJdbcService.getTasksCountByPriority())
                .anySatisfy(row -> {
                    assertThat(row.priority()).isEqualTo(Priority.HIGH);
                    assertThat(row.count()).isEqualTo(2);
                })
                .anySatisfy(row -> {
                    assertThat(row.priority()).isEqualTo(Priority.LOW);
                    assertThat(row.count()).isEqualTo(1);
                });
    }

    private Task task(String title) {
        return task(title, Priority.MEDIUM);
    }

    private Task task(String title, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("description");
        task.setCompleted(false);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(priority);
        return task;
    }
}
