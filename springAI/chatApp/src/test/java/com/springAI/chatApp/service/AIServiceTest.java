package com.springAI.chatApp.service;

import com.springAI.chatApp.services.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AIServiceTest {

    @Autowired
    private AIService aiService;


    @Test
    public void testAskAI(){
        var response = aiService.askAI("What is Spring AI?");
        System.out.println(response);
    }

    @Test
    public void testGetJoke(){
        var joke = aiService.getJoke("Cats");
        System.out.println(joke);
    }


    @Test
    public void testEmbedding(){
        var embed = aiService.getEmbedding("This is a big text");

        System.out.println("embed-length : " + embed.length);

        for(float e : embed){
            System.out.print(e + " ");
        }
    }

    @Test
    public void testStoreData(){
        aiService.ingestDataToVectorStore();
    }

    @Test
    public void testSimilaritySearch(){
        var res =  aiService.similaritySearch("Apple");
        for(var doc: res){
            System.out.println(doc);
        }
    }

}
