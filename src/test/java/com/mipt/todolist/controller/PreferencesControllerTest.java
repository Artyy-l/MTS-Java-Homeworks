package com.mipt.todolist.controller;

import com.mipt.todolist.dto.ViewPreferenceResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PreferencesControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url;

    @BeforeEach
    void setUp() {
        url = "http://localhost:" + port + "/api/preferences/view";
    }

    @Test
    void getView_setsDefaultCookieWhenAbsent() {
        ResponseEntity<ViewPreferenceResponseDto> r = restTemplate.getForEntity(url, ViewPreferenceResponseDto.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getMode()).isNotBlank();
    }

    @Test
    void postView_updatesMode() {
        ResponseEntity<ViewPreferenceResponseDto> r = restTemplate.postForEntity(
                url + "?mode=compact",
                null,
                ViewPreferenceResponseDto.class
        );
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(r.getBody()).isNotNull();
        assertThat(r.getBody().getMode()).isEqualTo("compact");
    }
}
