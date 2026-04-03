package com.example.ai.demo.service;

import reactor.core.publisher.Mono;

//Strategy Pattern
public interface AiService {

    Mono<String> chat(String question, String context);
    
}
