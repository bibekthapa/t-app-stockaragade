package com.example.ai.demo.data;



import java.util.List;

public class ChatResponse {
    private String reply;
    private List<String> sourcesUsed;  // which chunks were used to answer
    private long responseTimeMs;

    // Constructor
    public ChatResponse(String reply, List<String> sourcesUsed, long responseTimeMs) {
        this.reply = reply;
        this.sourcesUsed = sourcesUsed;
        this.responseTimeMs = responseTimeMs;
    }

    // Getters
    public String getReply() { return reply; }
    public List<String> getSourcesUsed() { return sourcesUsed; }
    public long getResponseTimeMs() { return responseTimeMs; }
}
