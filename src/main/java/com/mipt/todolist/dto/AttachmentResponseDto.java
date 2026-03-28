package com.mipt.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Метаданные вложения")
public class AttachmentResponseDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "photo.png")
    private String fileName;

    @Schema(example = "1024")
    private long size;

    @Schema(example = "2026-03-28T12:00:00")
    private LocalDateTime uploadedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
