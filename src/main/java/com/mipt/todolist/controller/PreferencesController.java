package com.mipt.todolist.controller;

import com.mipt.todolist.dto.ViewPreferenceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "Настройки отображения (cookie)")
public class PreferencesController {

    public static final String COOKIE_VIEW = "viewPreference";
    public static final String MODE_COMPACT = "compact";
    public static final String MODE_DETAILED = "detailed";
    private static final String DEFAULT_MODE = MODE_DETAILED;

    @Operation(summary = "Текущий режим отображения из cookie")
    @ApiResponse(responseCode = "200", description = "Режим")
    @GetMapping("/view")
    public ResponseEntity<ViewPreferenceResponseDto> getView(
            @CookieValue(value = COOKIE_VIEW, required = false) String mode,
            HttpServletResponse response) {
        String effective = (mode != null && !mode.isBlank()) ? mode : DEFAULT_MODE;
        if (mode == null || mode.isBlank()) {
            ResponseCookie cookie = ResponseCookie.from(COOKIE_VIEW, effective)
                    .path("/")
                    .httpOnly(false)
                    .build();
            response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
        }
        ViewPreferenceResponseDto dto = new ViewPreferenceResponseDto();
        dto.setMode(effective);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Установить режим отображения (cookie)")
    @ApiResponse(responseCode = "200", description = "Обновлено")
    @PostMapping("/view")
    public ResponseEntity<ViewPreferenceResponseDto> setView(
            @Parameter(description = "compact или detailed")
            @RequestParam String mode,
            HttpServletResponse response) {
        String normalized = MODE_COMPACT.equalsIgnoreCase(mode) ? MODE_COMPACT : MODE_DETAILED;
        Cookie cookie = new Cookie(COOKIE_VIEW, normalized);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        ViewPreferenceResponseDto dto = new ViewPreferenceResponseDto();
        dto.setMode(normalized);
        return ResponseEntity.ok(dto);
    }
}
