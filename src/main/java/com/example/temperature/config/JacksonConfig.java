package com.example.temperature.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.postConfigurer(objectMapper -> {
            if (objectMapper instanceof JsonMapper jsonMapper) {
                jsonMapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
            } else {
                objectMapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
            }
        });
    }
}
