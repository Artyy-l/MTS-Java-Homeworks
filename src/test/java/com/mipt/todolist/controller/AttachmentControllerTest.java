package com.mipt.todolist.controller;

import com.mipt.todolist.dto.AttachmentResponseDto;
import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.model.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AttachmentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String tasksUrl;
    private String api;

    @BeforeEach
    void setUp() {
        api = "http://localhost:" + port + "/api";
        tasksUrl = api + "/tasks";
    }

    @Test
    @DisplayName("загрузка, список, скачивание и удаление вложения")
    void attachmentLifecycle() {
        String taskId = createTask().getId();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource("hello".getBytes()) {
            @Override
            public String getFilename() {
                return "test.txt";
            }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<AttachmentResponseDto> post = restTemplate.postForEntity(
                api + "/tasks/" + taskId + "/attachments",
                new HttpEntity<>(body, headers),
                AttachmentResponseDto.class
        );
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(post.getBody()).isNotNull();
        Long attId = post.getBody().getId();

        ResponseEntity<AttachmentResponseDto[]> list = restTemplate.getForEntity(
                api + "/tasks/" + taskId + "/attachments",
                AttachmentResponseDto[].class
        );
        assertThat(list.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(list.getBody()).isNotNull();
        assertThat(list.getBody()).hasSize(1);

        ResponseEntity<Resource> get = restTemplate.getForEntity(
                api + "/attachments/" + attId,
                Resource.class
        );
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get.getBody()).isNotNull();

        ResponseEntity<Void> del = restTemplate.exchange(
                api + "/attachments/" + attId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getAfter = restTemplate.getForEntity(api + "/attachments/" + attId, String.class);
        assertThat(getAfter.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("загрузка для несуществующей задачи — 404")
    void upload_unknownTask_returns404() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource("x".getBytes()) {
            @Override
            public String getFilename() {
                return "f.bin";
            }
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<String> post = restTemplate.postForEntity(
                api + "/tasks/missing-task-id/attachments",
                new HttpEntity<>(body, headers),
                String.class
        );
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private TaskResponseDto createTask() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("att-task");
        dto.setDescription("d");
        dto.setPriority(Priority.MEDIUM);
        dto.setDueDate(LocalDate.now());
        ResponseEntity<TaskResponseDto> r = restTemplate.postForEntity(tasksUrl, dto, TaskResponseDto.class);
        assertThat(r.getBody()).isNotNull();
        return r.getBody();
    }
}
