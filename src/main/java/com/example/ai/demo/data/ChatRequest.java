package com.example.ai.demo.data;

public class ChatRequest {
    private String message;
    private String context ;

    private String documentSource;

    public String getDocumentSource() {
        return this.documentSource;
    }

    public void setDocumentSource(String documentSource) {
        this.documentSource = documentSource;
    };

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }



    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
