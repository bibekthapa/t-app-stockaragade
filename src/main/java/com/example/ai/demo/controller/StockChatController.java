package com.example.ai.demo.controller;



import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.ai.demo.data.ChatRequest;
import com.example.ai.demo.service.AiService;
import com.example.ai.demo.service.HuggingFaceService;
import com.example.ai.demo.service.IngestionService;
import com.example.ai.demo.service.RagChatService;

import reactor.core.publisher.Mono;



@RestController
@RequestMapping("/api")
public class StockChatController {

    private static final Logger log = LoggerFactory.getLogger(StockChatController.class);
   private final Map<String,AiService> aiServices;

   private final RagChatService ragChatService;
    private final IngestionService ingestionService;


    public StockChatController(Map<String,AiService> aiServices,RagChatService ragChatService, IngestionService ingestionService) {
        this.ragChatService = ragChatService;
        this.ingestionService = ingestionService;
        this.aiServices = aiServices;
    }

    @PostMapping("/chat")
    public Mono<Map<String,String>> chat(@RequestBody ChatRequest message , @RequestParam String provider) {
        String prompt = message.getMessage();
        String context = message.getContext();
       AiService service = aiServices.get(provider);
        
       if(service == null) throw new IllegalArgumentException("Unknown Provider: " + provider);
       return service.chat(prompt, context).map(reply -> Map.of("reply" , reply));
    }

     @PostMapping("/ragchat")
    public Mono<Map<String,String>> ragchat(@RequestBody ChatRequest message , @RequestParam String provider) {
       
        String prompt = message.getMessage();

        AiService service = aiServices.get(provider);
        if(service == null) throw new IllegalArgumentException("Unknown Provider :" + provider);

        return ragChatService.chat(prompt).map(chatResponse -> Map.of("reply" , chatResponse.getReply()));
        
    }

    @PostMapping(value = "/ingest",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> ingest(@RequestPart("file") FilePart file) {
       if(file.filename().isEmpty()){
        return Mono.just(ResponseEntity.badRequest().body(Map.of("error","File is Empty")));
       }
       
     return DataBufferUtils.join(file.content())
            .flatMap(dataBuffer ->{
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    try{
                            int chunksIngested = ingestionService.ingestPDF(bytes,file.filename());
                            return Mono.just(ResponseEntity.ok(Map.of(
                                "message", "Document ingested successfully",
                                "filename", file.filename(),
                                "chunksStored", chunksIngested
                        )));
                    }catch(IOException e){
                         log.error("Failed to ingest document", e);
                        return Mono.just(ResponseEntity.internalServerError()
                                .body(Map.of("error", "Failed to process document: " + e.getMessage())));
                    }
            });
            
            
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
    
   

}

