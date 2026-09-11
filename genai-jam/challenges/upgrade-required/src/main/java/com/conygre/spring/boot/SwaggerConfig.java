package com.conygre.spring.boot;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenAPI configuration for automatic API documentation.
 * Uses SpringDoc OpenAPI (modern replacement for SpringFox/Swagger 2).
 * Swagger UI will be available at: http://localhost:8080/swagger-ui.html
 */
@Configuration
@Profile("!test") // Disable Swagger config for tests to avoid initialization issues
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Album REST API")
                        .description("This API allows you to interact with albums. It is a CRUD API for managing a compact disc catalog.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nick Todd")
                                .url("http://www.conygre.com")
                                .email("nick.todd@conygre.com")));
    }
}
