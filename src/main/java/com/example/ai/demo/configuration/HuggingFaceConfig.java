package com.example.ai.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Configuration

public class HuggingFaceConfig {

    @Value("${spring.ai.huggingface.api-key}")
    private String apiToken;

    @Bean
    public WebClient huggingFaceClient() {

      
        return WebClient.builder()
                .baseUrl("https://router.huggingface.co/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .build();
        
        
    }
}
