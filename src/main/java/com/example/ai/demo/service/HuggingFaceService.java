package com.example.ai.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service("hf")
public class HuggingFaceService implements AiService{

   private final WebClient webClient;


    public HuggingFaceService(WebClient webClient) {
        this.webClient = webClient;
    }

   
    @Override
    public Mono<String> chat(String prompt, String context) {
        Map<String, Object> request = Map.of(
            "model", "HuggingFaceTB/SmolLM3-3B:hf-inference",
            "messages", List.of(
                Map.of("role","system","content","You are a helpful assistant."),
                Map.of("role","user","content", prompt)
            )
        );

        return webClient.post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return (String) message.get("content");
                    }
                    return "No response";
                });
    }

    
    
}
