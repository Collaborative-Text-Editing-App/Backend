package com.team13.CollaborativeEditor.dto;

public class ImportDocumentRequest {
    private String content;

    public ImportDocumentRequest() {}
    public ImportDocumentRequest(String content) {
        this.content = content;
    }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}