package com.springAI.chatApp.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AIService {

    @Autowired
    private ChatClient chatClient;

    public String getJoke(String topic){

        String systemPrompt = """
                You are a sarcastic joker.
                Please give jokes in one line.
                Give me a joke on the topic : {topic}
                """;
        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        String renderText = promptTemplate.render(
                Map.of("topic",topic)
        );
        var response =  chatClient.prompt()
                .user(renderText)
                .advisors(
                        new SimpleLoggerAdvisor()
                )
                .call().chatClientResponse();

        return response.chatResponse().getResult().getOutput().getText();
    }

}
