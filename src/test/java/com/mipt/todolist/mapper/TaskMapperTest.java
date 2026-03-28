package com.mipt.todolist.mapper;

import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.dto.TaskUpdateDto;
import com.mipt.todolist.model.Priority;
import com.mipt.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void toEntity_setsCreatedAtAndTags() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("abc");
        dto.setDescription("d");
        dto.setDueDate(LocalDate.now());
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("a", "b"));
        Task task = taskMapper.toEntity(dto);
        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getTags()).containsExactlyInAnyOrder("a", "b");
        assertThat(task.isCompleted()).isFalse();
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void updateEntity_nullsIgnored() {
        Task task = new Task();
        task.setId("x");
        task.setTitle("old");
        task.setDescription("oldd");
        task.setCompleted(false);
        task.setCreatedAt(java.time.LocalDateTime.now().minusDays(1));
        task.setDueDate(LocalDate.now());
        task.setPriority(Priority.LOW);
        task.getTags().add("t");
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("new title");
        taskMapper.updateEntity(dto, task);
        assertThat(task.getTitle()).isEqualTo("new title");
        assertThat(task.getDescription()).isEqualTo("oldd");
    }

    @Test
    void toResponseDto_mapsAll() {
        Task task = new Task("id1", "t", "d", true,
                java.time.LocalDateTime.now(), LocalDate.now(), Priority.MEDIUM, Set.of("x"));
        TaskResponseDto dto = taskMapper.toResponseDto(task);
        assertThat(dto.getId()).isEqualTo("id1");
        assertThat(dto.isCompleted()).isTrue();
        assertThat(dto.getTags()).contains("x");
    }
}
