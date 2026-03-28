package com.mipt.todolist.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${app.api.version:2.0.0}")
    private String apiVersion;

    @Bean
    public OpenAPI toDoListOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("To-Do List API")
                        .version(apiVersion)
                        .description("REST API для управления задачами, вложениями и избранным")
                        .contact(new Contact().name("MIPT").email("support@example.com")));
    }
}
