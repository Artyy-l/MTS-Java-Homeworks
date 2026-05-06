package com.mipt.todolist.repository;

import com.mipt.todolist.model.Priority;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.model.TaskAttachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository attachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByCompletedAndPriority_filtersTasks() {
        Task matching = task("matching", false, Priority.HIGH, LocalDate.now().plusDays(2));
        Task other = task("other", true, Priority.HIGH, LocalDate.now().plusDays(2));
        taskRepository.saveAll(List.of(matching, other));

        List<Task> result = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);

        assertThat(result).extracting(Task::getId).contains(matching.getId());
        assertThat(result).extracting(Task::getId).doesNotContain(other.getId());
    }

    @Test
    void findTasksDueWithinNextSevenDays_usesCustomQuery() {
        Task soon = task("soon", false, Priority.MEDIUM, LocalDate.now().plusDays(3));
        Task later = task("later", false, Priority.MEDIUM, LocalDate.now().plusDays(10));
        taskRepository.saveAll(List.of(soon, later));

        List<Task> result = taskRepository.findTasksDueWithinNextSevenDays(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        assertThat(result).extracting(Task::getId).contains(soon.getId());
        assertThat(result).extracting(Task::getId).doesNotContain(later.getId());
    }

    @Test
    void savesTaskWithAttachmentAndLoadsWithEntityGraph() {
        Task task = task("with attachment", false, Priority.LOW, LocalDate.now().plusDays(1));
        taskRepository.saveAndFlush(task);

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task);
        attachment.setFileName("note.txt");
        attachment.setStoredFileName("stored-note");
        attachment.setContentType("text/plain");
        attachment.setSize(42);
        attachment.setUploadedAt(LocalDateTime.now());
        attachmentRepository.saveAndFlush(attachment);
        entityManager.clear();

        List<Task> tasks = taskRepository.findAllWithAttachments();

        Task loaded = tasks.stream()
                .filter(t -> t.getId().equals(task.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(loaded.getAttachments()).hasSize(1);
        assertThat(loaded.getAttachments().getFirst().getFileName()).isEqualTo("note.txt");
    }

    private Task task(String title, boolean completed, Priority priority, LocalDate dueDate) {
        Task task = new Task();
        task.setId(UUID.randomUUID().toString());
        task.setTitle(title);
        task.setDescription("description");
        task.setCompleted(completed);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setTags(Set.of("test"));
        return task;
    }
}
