package com.mipt.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.model.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerValidationMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/tasks с невалидным телом — 400 и детали полей")
    void create_invalid_returns400WithDetails() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("ab");
        dto.setPriority(Priority.MEDIUM);
        dto.setDueDate(LocalDate.now());
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.details.fieldErrors").exists());
    }
}
