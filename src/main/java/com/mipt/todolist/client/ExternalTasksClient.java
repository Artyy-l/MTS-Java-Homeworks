package com.mipt.todolist.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.todolist.dto.TaskCreateDto;
import com.mipt.todolist.dto.TaskResponseDto;
import com.mipt.todolist.exception.ExternalApiException;
import com.mipt.todolist.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);
    private static final int MAX_LOGGED_BODY_LENGTH = 500;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ExternalTasksClient(RestClient externalTasksRestClient, ObjectMapper objectMapper) {
        this.restClient = externalTasksRestClient;
        this.objectMapper = objectMapper;
    }

    public CreatedTask createTask(TaskCreateDto request) {
        ResponseEntity<TaskResponseDto> response = restClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (clientRequest, clientResponse) -> {
                    throw taskNotFound(readBody(clientResponse));
                })
                .onStatus(status -> status.is5xxServerError(), (clientRequest, clientResponse) -> {
                    throw externalApiException(clientResponse.getStatusCode().value(), readBody(clientResponse),
                            clientResponse.getHeaders().getContentType());
                })
                .toEntity(TaskResponseDto.class);

        return new CreatedTask(response.getBody(), response.getHeaders().getLocation());
    }

    public TaskResponseDto getTask(String id) {
        return restClient.get()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (clientRequest, clientResponse) -> {
                    throw taskNotFound(readBody(clientResponse));
                })
                .onStatus(status -> status.is5xxServerError(), (clientRequest, clientResponse) -> {
                    throw externalApiException(clientResponse.getStatusCode().value(), readBody(clientResponse),
                            clientResponse.getHeaders().getContentType());
                })
                .body(TaskResponseDto.class);
    }

    public List<TaskResponseDto> getTasks(Boolean completed, Integer limit) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/tasks");
                    if (completed != null) {
                        uriBuilder.queryParam("completed", completed);
                    }
                    if (limit != null) {
                        uriBuilder.queryParam("limit", limit);
                    }
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), (clientRequest, clientResponse) -> {
                    throw externalApiException(clientResponse.getStatusCode().value(), readBody(clientResponse),
                            clientResponse.getHeaders().getContentType());
                })
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void deleteTask(String id) {
        restClient.delete()
                .uri("/tasks/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (clientRequest, clientResponse) -> {
                    throw taskNotFound(readBody(clientResponse));
                })
                .onStatus(status -> status.is5xxServerError(), (clientRequest, clientResponse) -> {
                    throw externalApiException(clientResponse.getStatusCode().value(), readBody(clientResponse),
                            clientResponse.getHeaders().getContentType());
                })
                .toBodilessEntity();
    }

    public String callUnstable(String mode) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/unstable")
                        .queryParam("mode", mode)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), (clientRequest, clientResponse) -> {
                    throw externalApiException(clientResponse.getStatusCode().value(), readBody(clientResponse),
                            clientResponse.getHeaders().getContentType());
                })
                .body(String.class);
    }

    private TaskNotFoundException taskNotFound(byte[] body) {
        ProblemDetail problemDetail = parseProblemDetail(body);
        String detail = problemDetail != null && problemDetail.getDetail() != null
                ? problemDetail.getDetail()
                : "Task not found in external API";
        return TaskNotFoundException.withMessage(detail);
    }

    private ExternalApiException externalApiException(int statusCode, byte[] body, MediaType contentType) {
        if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            log.warn("Unexpected external API content type: {}, body={}", contentType, limitedBody(body));
        }
        return new ExternalApiException("External API returned status " + statusCode, statusCode);
    }

    private ProblemDetail parseProblemDetail(byte[] body) {
        if (body == null || body.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(body, ProblemDetail.class);
        } catch (IOException ex) {
            log.warn("Could not parse external API ProblemDetail body={}", limitedBody(body));
            return null;
        }
    }

    private byte[] readBody(org.springframework.http.client.ClientHttpResponse response) throws IOException {
        return response.getBody().readAllBytes();
    }

    private String limitedBody(byte[] body) {
        if (body == null || body.length == 0) {
            return "";
        }
        String value = new String(body, StandardCharsets.UTF_8).replaceAll("\\s+", " ").trim();
        if (value.length() <= MAX_LOGGED_BODY_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_LOGGED_BODY_LENGTH) + "...";
    }

    public record CreatedTask(TaskResponseDto task, URI location) {
    }
}
