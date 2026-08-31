package com.springAI.chatApp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {

    @Autowired
    private ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    @Autowired
    private ChatMemory chatMemory;

    @Value("classpath:faq.pdf")
    Resource pdfFile;


    public String askAIWithAdvisor(String prompt, String userId){


        VectorStoreChatMemoryAdvisor memoryAdvisor =
                VectorStoreChatMemoryAdvisor.builder(vectorStore)
                        .defaultTopK(4)
                        .build();
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(
                        SearchRequest.builder()
                                .filterExpression("file_name == 'faq.pdf'")
                                .build()
                )
                .build();

        SafeGuardAdvisor safeGuardAdvisor = new SafeGuardAdvisor(List.of("politics","Gaming"));

        return chatClient.prompt()
                .system("""
                        You are an AI assistant called Cody.
                        Greet user with your name (Cody) and the user name if you know their name.
                        Answer in a friendly, conversational tone.
                        """)
                .user(prompt)
                .advisors(safeGuardAdvisor,chatMemoryAdvisor,memoryAdvisor,questionAnswerAdvisor)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        userId
                ))
                .call()
                .content();

    }

    public String askAI(String prompt){

        String template = """
                You are an AI Assistant helping a developer.
                
                Rules:
                - Use ONLY the information provided in the context.
                - You may rephrase, summarize and explain in natural language.
                - Do NOT introduce new concepts or facts
                - If multiple context sections are relevant, combine them into a single explanation.
                - If the answer is not present, say "I don't know."
                
                Context:
                {context}
                
                Answer in a friendly, conversational tone.
                """;

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(prompt)
                .topK(4)
                .similarityThreshold(0.4)
                .filterExpression("file_name == 'faq.pdf'")
                .build());

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        PromptTemplate promptTemplate = new PromptTemplate(template);
        String systemPrompt = promptTemplate.render(Map.of("context",context));
        return chatClient.prompt()
                .system(systemPrompt)
                .user(prompt)
                .advisors()
                .call()
                .content();
    }

    public void ingestPdfToVectorStore(){
        //Extract
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfFile);
        List<Document> pages = reader.get();

        //Transform
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();
        List<Document> chunks = tokenTextSplitter.apply(pages);

        //Load
        vectorStore.add(chunks);
    }


    public static List<Document> springAIDocs(){
        return List.of(
                new Document(
                        "Spring AI provides abstractions like ChatClient, ChatModel, and EmbeddingModel to interact with LLMs.",
                        Map.of("topic", "ai")
                ),

                new Document(
                        "A VectorStore is used to persist embeddings and perform similarity search for retrieval augmented generation.",
                        Map.of("topic", "vectorstore")
                ),

                new Document(
                        "Retrieval Augmented Generation combines vector similarity search with prompt augmentation to reduce hallucinations and provide context to language models.",
                        Map.of("topic", "rag")
                ),

                new Document(
                        "PgVectorStore stores embeddings inside PostgreSQL using the pgvector extension.",
                        Map.of("topic", "vectorstore")
                ),

                new Document(
                        "ChatClient provides a fluent API to send prompts to language models like OpenAI or Ollama.",
                        Map.of("topic", "ai")
                )
        );
    }

}
