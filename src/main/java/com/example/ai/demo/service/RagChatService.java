package com.example.ai.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.example.ai.demo.data.ChatResponse;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RagChatService {

    private static final Logger log = LoggerFactory.getLogger(RagChatService.class);

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    // RAG tuning parameters
    private static final int TOP_K = 5;                   // how many chunks to retrieve
    private static final double SIMILARITY_THRESHOLD = 0.4; // minimum relevance score

    public RagChatService(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        // ChatClient.Builder auto-configures Claude from application.yml
        this.chatClient = chatClientBuilder.build();
    }


    public Mono<ChatResponse> chat(String userMessage) {

            return Mono.
            fromCallable(()-> executeRagPipeline(userMessage)).subscribeOn(Schedulers.boundedElastic());
        
        }


    private ChatResponse executeRagPipeline(String userMessage){
        long startTime = System.currentTimeMillis();

        log.info("Processing question: {}", userMessage);

        // Step 1: Embed the user question and search pgvector
        // Spring AI automatically embeds userMessage using OpenAI
        // then does cosine similarity search against stored chunks
         List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(userMessage)
                .withTopK(TOP_K)
                .withSimilarityThreshold(SIMILARITY_THRESHOLD)
        );

        log.info("Retrieved {} relevant chunks from pgvector", relevantDocs.size());

        if (relevantDocs.isEmpty()) {
            // No relevant context found
            return new ChatResponse(
                "I don't have enough information in my knowledge base to answer that question.",
                List.of(),
                System.currentTimeMillis() - startTime
            );
        }
            String context = relevantDocs.stream().map(Document::getContent).collect(Collectors.joining("\n\n--\n\n"));

        List<String> sources = relevantDocs.stream().map(doc -> (String) doc.getMetadata().getOrDefault("source", "unknown"))
                                .distinct().collect(Collectors.toList());
        
        String prompt = buildRagPrompt(context, userMessage);

        String reply = chatClient.prompt().user(prompt).call().content();
        long responseTime = System.currentTimeMillis() - startTime;
        log.info("Got reply from Claude in {}ms", responseTime);

        return new ChatResponse(reply, sources, responseTime);
    }
    

    private String buildRagPrompt(String context, String question) {
        return """
            You are a helpful assistant that answers questions based on provided context.
            
            INSTRUCTIONS:
            - Answer ONLY using the information in the CONTEXT section below
            - If the answer is not in the context, say "I don't have that information in my knowledge base"
            - Be concise and accurate
            - If you quote from the context, keep it brief
            
            CONTEXT:
            %s
            
            QUESTION:
            %s
            
            ANSWER:
            """.formatted(context, question);
    }
    
}
