package com.springAI.chatApp.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIconfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder.build();
    }
}
