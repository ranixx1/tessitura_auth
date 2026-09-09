package com.tessitura.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Authentication Service API")
                        .version("1.0")
                        .description(
                            "API responsável por autenticação, JWT, recuperação de senha e usuários."
                        )
                )
                .addSecurityItem(
                    new SecurityRequirement()
                            .addList("bearerAuth")
                )
                .schemaRequirement(
                    "bearerAuth",
                    new SecurityScheme()
                            .name("bearerAuth")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                );
    }
}