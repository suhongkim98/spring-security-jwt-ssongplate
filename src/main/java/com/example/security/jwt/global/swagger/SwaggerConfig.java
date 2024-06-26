package com.example.security.jwt.global.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        Info info = new Info()
                .title("ssongplate swagger")
                .description("ssongplate 설명")
                .version("1.0.0")
                .contact(new Contact().email("ssong@ssongssong.com"));

        List<Server> servers = List.of(
                new Server().url("http://localhost:8080")
        );
        return new OpenAPI()
                .info(info)
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearer-key",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .servers(servers);
    }
}
