package com.gft.packagelocker.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI packageLockerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Package Locker System API")
                        .version("0.0.1")
                        .description("MVP backend API for package locker delivery flows."));
    }
}
