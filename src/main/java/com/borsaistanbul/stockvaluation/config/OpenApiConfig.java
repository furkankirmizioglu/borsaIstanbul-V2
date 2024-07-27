package com.borsaistanbul.stockvaluation.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi valuationApiGroup() {
        return GroupedOpenApi.builder()
                .group("Borsa Istanbul Backend")
                .pathsToMatch("/**")
                .packagesToScan("com.borsaistanbul.stockvaluation")
                .build();
    }
}
