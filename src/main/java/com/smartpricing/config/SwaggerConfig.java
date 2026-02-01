package com.smartpricing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Smart Price Negotiation API")
                        .description("Backend APIs for AI-powered eCommerce price negotiation")
                        .version("1.0.0"));
    }
}
