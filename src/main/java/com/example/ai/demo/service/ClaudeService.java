package com.example.ai.demo.service;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service("claude")
public class ClaudeService implements AiService {

   private final ChatClient chatClient;

   public ClaudeService(AnthropicChatModel chatModel ){
        this.chatClient = ChatClient.create(chatModel);
   }

    

    @Override
    public Mono<String> chat(String question, String context) {
        String userMessage = "Context: " + context + "\n\nQuestion: " + question;

        return Mono.fromCallable(() ->
            chatClient.prompt()
                .system("You are a stock market assistant. You ONLY answer questions about stocks, trading, market trends, and financial analysis. " +
                        "If the question is not related to finance or the stock market, respond ONLY with: " +
                        "\"I can only help with stock market and finance related questions.\" Do not elaborate.")
                .user(userMessage)
                .call()
                .content());
    }
    
}
