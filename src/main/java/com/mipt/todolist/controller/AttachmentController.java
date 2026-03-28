package com.mipt.todolist.controller;

import com.mipt.todolist.dto.AttachmentResponseDto;
import com.mipt.todolist.model.TaskAttachment;
import com.mipt.todolist.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Attachments", description = "Вложения к задачам")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @Operation(summary = "Загрузить файл к задаче")
    @ApiResponse(responseCode = "201", description = "Файл сохранён")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachmentResponseDto> upload(
            @Parameter(description = "Идентификатор задачи") @PathVariable("taskId") String taskId,
            @Parameter(description = "Файл") @RequestPart("file") MultipartFile file) throws IOException {
        AttachmentResponseDto body = attachmentService.storeAttachment(taskId, file);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(body);
    }

    @Operation(summary = "Скачать вложение")
    @ApiResponse(responseCode = "200", description = "Файл")
    @ApiResponse(responseCode = "404", description = "Не найдено")
    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> download(
            @Parameter(description = "Идентификатор вложения") @PathVariable("attachmentId") Long attachmentId)
            throws MalformedURLException {
        TaskAttachment meta = attachmentService.getAttachment(attachmentId);
        Resource resource = attachmentService.loadAsResource(attachmentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(meta.getContentType()))
                .body(resource);
    }

    @Operation(summary = "Удалить вложение")
    @ApiResponse(responseCode = "204", description = "Удалено")
    @ApiResponse(responseCode = "404", description = "Не найдено")
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> delete(@PathVariable("attachmentId") Long attachmentId) throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Список вложений задачи")
    @ApiResponse(responseCode = "200", description = "Список метаданных")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> list(
            @Parameter(description = "Идентификатор задачи") @PathVariable("taskId") String taskId) {
        return ResponseEntity.ok(attachmentService.listByTaskId(taskId));
    }
}
