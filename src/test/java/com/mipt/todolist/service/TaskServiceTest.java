package com.mipt.todolist.service;

import com.mipt.todolist.model.Priority;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TaskService.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @BeforeEach
    void resetMocks() {
        reset(taskRepository);
    }

    @Test
    void bulkCompleteTasks_updatesExistingTaskStatus() {
        Task task = task("task-1");
        when(taskRepository.findAllById(List.of(task.getId()))).thenReturn(List.of(task));

        taskService.bulkCompleteTasks(List.of(task.getId()));

        verify(taskRepository).findAllById(List.of(task.getId()));
        verify(taskRepository).saveAll(List.of(task));
        assertThat(task.isCompleted()).isTrue();
        verifyNoMoreInteractions(taskRepository);
    }

    private Task task(String id) {
        Task task = new Task();
        task.setId(id);
        task.setTitle("Write tests");
        task.setDescription("Cover service layer");
        task.setCompleted(false);
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setPriority(Priority.MEDIUM);
        return task;
    }
}
