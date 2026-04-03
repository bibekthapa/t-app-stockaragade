package com.example.ai.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;
    private static final int CHUNK_SIZE = 500;
    private static final int CHUNK_OVERLAP = 50 ;

    public IngestionService(VectorStore vectorStore){
        this.vectorStore = vectorStore;
    }

    public int ingestPDF(byte[] fileBytes , String fileName) throws IOException{
        log.info("Starting ingestion for file: {}", fileName);
        String rawText = extractToTextFromPDF(fileBytes);
        log.info("Extracted {} characters from PDF", rawText.length());

         List<String> chunks = chunkText(rawText);
        log.info("Split into {} chunks", chunks.size());

        List<Document> documents = new ArrayList<>();
        
        for(int i = 0 ; i < chunks.size() ; i++){
            Document doc = new Document(chunks.get(i), Map.of("source" , fileName, "chunkIndex" , i , "totalChunks" , chunks.size() ));

            documents.add(doc);
        }
        
        vectorStore.add(documents);
        log.info("Successfully ingested {} chunks into pgvector", documents.size());

        return documents.size();
    }

    private List<String> chunkText(String text) {
        
        List<String> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");

        int i = 0 ; 
        
        while(i < words.length){
            int end = Math.min( i + CHUNK_SIZE , words.length);
            StringBuilder chunk = new StringBuilder();

            for(int j = i ; j < end ; j++){

                if(j > i) chunk.append(" ");
                chunk.append(words[j]);

            }
            chunks.add(chunk.toString());

            i += CHUNK_SIZE - CHUNK_OVERLAP ;
        }

        return chunks;
    }

    private String extractToTextFromPDF(byte[] fileBytes)  {
        
        try(PDDocument document = Loader.loadPDF(fileBytes)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }catch(IOException e){
            log.error("Failed to extract from pdf" , e) ; 
            throw new RuntimeException("Failed to extract from PDF" , e);
        }
    
    }
    
}
