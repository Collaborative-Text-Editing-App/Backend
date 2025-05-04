package com.team13.CollaborativeEditor.dto;

import java.util.Map;
import com.team13.CollaborativeEditor.models.Cursor;

public class DocumentUpdateMessage {
    private String documentId;
    private String content;
    private Map<String, Cursor> cursors;
    
    // Getters and setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Cursor> getCursors() {
        return cursors;
    }

    public void setCursors(Map<String, Cursor> cursors) {
        this.cursors = cursors;
    }
}