package com.mipt.todolist.repository;

import com.mipt.todolist.model.Priority;
import com.mipt.todolist.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("""
            select t
            from Task t
            where t.dueDate between :from and :to
            """)
    List<Task> findTasksDueWithinNextSevenDays(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @EntityGraph(attributePaths = "attachments")
    @Query("select distinct t from Task t")
    List<Task> findAllWithAttachments();
}
