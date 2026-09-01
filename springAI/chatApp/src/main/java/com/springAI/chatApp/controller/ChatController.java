package com.springAI.chatApp.controller;

import com.springAI.chatApp.tools.FlightBookingTools;
import com.springAI.chatApp.tools.TravellingTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    private final TravellingTools travellingTools;

    private final FlightBookingTools flightBookingTool;

    @Autowired
    private ChatMemory chatMemory;


    @PostMapping("/chat")
    public String chat(@RequestBody String message, @RequestParam String userId){

        String systemPrompt = String.format("""
            You are a friendly flight booking assistant.
            Use the available tools to create, view, or update bookings.
            Always confirm actions with the user when possible.
            
            IMPORTANT: The current user's ID is "%s".
            When calling tools that require a userId, ALWAYS use this exact value.
            """, userId);

        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .tools(travellingTools,flightBookingTool)
                .advisors(chatMemoryAdvisor)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        userId
                ))
                .call()
                .content();

    }


}
