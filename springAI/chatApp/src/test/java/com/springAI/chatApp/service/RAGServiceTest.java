package com.springAI.chatApp.service;

import com.springAI.chatApp.services.RAGService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RAGServiceTest {

    @Autowired
    private RAGService ragService;

    @Test
    public void testAskAI(){
        var response = ragService.askAI("How to connect to my discord account?");
        System.out.println(response);
    }


    @Test
    public void testAskAIWithAdvisor(){
        var response = ragService.askAIWithAdvisor("what are your views on mobile gaming?","bishal0x01");
        System.out.println(response);
    }


    @Test
    public void testIngest(){
        ragService.ingestPdfToVectorStore();
    }
}
