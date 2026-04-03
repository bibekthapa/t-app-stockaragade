package com.example.ai.demo.configuration;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.huggingface.HuggingfaceChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {
    
    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(AnthropicChatModel anthropicChatModel){
        return ChatClient.builder(anthropicChatModel);
    }

    @Bean
    @Qualifier
    public ChatClient.Builder anthropicChatClientBuilder(AnthropicChatModel anthropicChatModel){
        return ChatClient.builder(anthropicChatModel);
    }

    @Bean
    @Qualifier
    public ChatClient.Builder huggingfaceChatClientBuilder(HuggingfaceChatModel huggingfaceChatModel){
        return ChatClient.builder(huggingfaceChatModel);
    }
}
