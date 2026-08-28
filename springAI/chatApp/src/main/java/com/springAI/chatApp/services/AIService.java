package com.springAI.chatApp.services;


import com.springAI.chatApp.dto.Joke;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    @Autowired
    private ChatClient chatClient;

    private final EmbeddingModel embeddingModel;

    private final VectorStore vectorStore;

    public List<Document> similaritySearch(String text){
        return vectorStore.similaritySearch(SearchRequest.builder()
                        .query(text)
                        .topK(3)
                        .similarityThreshold(0.3)
                .build());
    }

    public String askAI(String prompt){
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
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

    public void ingestDataToVectorStore() {

        List<Document> movies = List.of(

                new Document(
                        "A thief who steals corporate secrets through the use of dream-sharing technology.",
                        Map.of(
                                "title", "Inception",
                                "genre", "Sci-Fi",
                                "year", "2010"
                        )
                ),

                new Document(
                        "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                        Map.of(
                                "title", "Interstellar",
                                "genre", "Sci-Fi",
                                "year", "2014"
                        )
                ),

                new Document(
                        "A poor yet passionate young man falls in love with a rich young woman, giving her a sense of freedom.",
                        Map.of(
                                "title", "The Notebook",
                                "genre", "Romance",
                                "year", "2004"
                        )
                ),

                new Document(
                        "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
                        Map.of(
                                "title", "The Matrix",
                                "genre", "Sci-Fi",
                                "year", "1999"
                        )
                ),

                new Document(
                        "A young wizard discovers that he is a famous wizard and begins his journey at a magical school.",
                        Map.of(
                                "title", "Harry Potter",
                                "genre", "Fantasy",
                                "year", "2001"
                        )
                )
        );

        vectorStore.add(movies);
        vectorStore.add(springAIDocs());

    }

    public float[] getEmbedding(String text){
        return embeddingModel.embed(text);
    }

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
                .call()
                .entity(Joke.class);

        return response.text();
    }

}
