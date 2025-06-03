package com.uams.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI userAddressOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Address Management System API")
                        .description("API documentation for managing users and their addresses")
                        .version("1.0")
                        .contact(new Contact()
                                .name("UAMS Team")
                                .email("support@uams.com")));
    }
} 