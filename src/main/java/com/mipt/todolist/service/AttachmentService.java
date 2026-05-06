package com.mipt.todolist.service;

import com.mipt.todolist.dto.AttachmentResponseDto;
import com.mipt.todolist.exception.AttachmentNotFoundException;
import com.mipt.todolist.exception.TaskNotFoundException;
import com.mipt.todolist.model.Task;
import com.mipt.todolist.model.TaskAttachment;
import com.mipt.todolist.repository.TaskAttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private final Path uploadDirectory;

    public AttachmentService(TaskAttachmentRepository attachmentRepository,
                             TaskService taskService,
                             @Value("${app.uploads.directory:uploads}") String uploadsDir) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.uploadDirectory = Path.of(uploadsDir);
    }

    @Transactional
    public AttachmentResponseDto storeAttachment(String taskId, MultipartFile file) throws IOException {
        Task task = taskService.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));
        Files.createDirectories(uploadDirectory);
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String storedName = UUID.randomUUID().toString();
        Path target = uploadDirectory.resolve(storedName);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        TaskAttachment meta = new TaskAttachment();
        meta.setTask(task);
        meta.setFileName(originalName);
        meta.setStoredFileName(storedName);
        meta.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        meta.setSize(file.getSize());
        meta.setUploadedAt(LocalDateTime.now());
        TaskAttachment saved = attachmentRepository.save(meta);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
    }

    public Resource loadAsResource(Long attachmentId) throws MalformedURLException {
        TaskAttachment meta = getAttachment(attachmentId);
        Path file = uploadDirectory.resolve(meta.getStoredFileName());
        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new AttachmentNotFoundException(attachmentId);
        }
        return resource;
    }

    @Transactional
    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment meta = getAttachment(attachmentId);
        Path file = uploadDirectory.resolve(meta.getStoredFileName());
        Files.deleteIfExists(file);
        attachmentRepository.deleteById(attachmentId);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponseDto> listByTaskId(String taskId) {
        if (!taskService.existsById(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        return attachmentRepository.findByTask_Id(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private AttachmentResponseDto toDto(TaskAttachment a) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(a.getId());
        dto.setFileName(a.getFileName());
        dto.setSize(a.getSize());
        dto.setUploadedAt(a.getUploadedAt());
        return dto;
    }
}
