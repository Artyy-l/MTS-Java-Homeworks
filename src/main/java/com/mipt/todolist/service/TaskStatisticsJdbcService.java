package com.mipt.todolist.service;

import com.mipt.todolist.dto.TaskPriorityCountDto;
import com.mipt.todolist.model.Priority;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatisticsJdbcService {

    private static final RowMapper<TaskPriorityCountDto> PRIORITY_COUNT_ROW_MAPPER = (rs, rowNum) ->
            new TaskPriorityCountDto(Priority.valueOf(rs.getString("priority")), rs.getLong("tasks_count"));

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskPriorityCountDto> getTasksCountByPriority() {
        return jdbcTemplate.query("""
                select priority, count(*) as tasks_count
                from tasks
                group by priority
                order by priority
                """, PRIORITY_COUNT_ROW_MAPPER);
    }
}
