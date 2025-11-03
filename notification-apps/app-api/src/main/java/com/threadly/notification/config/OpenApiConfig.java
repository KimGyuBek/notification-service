package com.threadly.notification.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("Threadly Notification-Service API")
            .version("v1")
            .description("Threadly Notification-Service API 명세"))
        .components(
            new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

  }
  @Bean
  public GroupedOpenApi notificationsApi() {
    return GroupedOpenApi.builder()
        .group("notifications")
        .packagesToScan("com.threadly.notification.controller")
        .pathsToMatch("/api/notifications/**")
        .build();
  }

}
