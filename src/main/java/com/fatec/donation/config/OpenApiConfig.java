package com.fatec.donation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI donationAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("DoNation API")
                        .description("DoNation - Backend API")
                        .version("v0.0.1")
                        .license(new License()
                                .name("Apache License Version 2.0")
                                .url("https://github.com/WolneiACordeiro/DoNation-1618-S5")));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateSwaggerJson() throws IOException {
        String swaggerUrl = "http://localhost:8080/v3/api-docs";
        String outputPath = "./swagger.json";

        try {
            ResponseEntity<String> response = new RestTemplate().getForEntity(swaggerUrl, String.class);
            try (FileWriter fileWriter = new FileWriter(outputPath)) {
                fileWriter.write(response.getBody());
            }
            System.out.println("Swagger.json exportado para: " + outputPath);
        } catch (Exception e) {
            System.err.println("Erro ao exportar swagger.json: " + e.getMessage());
        }
    }

}