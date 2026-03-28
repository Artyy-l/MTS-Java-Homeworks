package com.mipt.todolist.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Режим отображения списка задач из cookie")
public class ViewPreferenceResponseDto {

    @Schema(example = "compact", allowableValues = {"compact", "detailed"})
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
