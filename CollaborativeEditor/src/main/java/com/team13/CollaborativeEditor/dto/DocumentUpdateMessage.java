package com.team13.CollaborativeEditor.dto;

import java.sql.Timestamp;
import java.util.Map;
import com.team13.CollaborativeEditor.models.Cursor;

public class DocumentUpdateMessage {
    private String id;
    private String editorCode;
    private String viewerCode;
    private String content;
    private Map<String, Cursor> activeUsers;
    private Timestamp lastModified;

    public DocumentUpdateMessage(){}

    public DocumentUpdateMessage(String id, String editorCode, String viewerCode, String content,
                                 Map<String, Cursor> activeUsers, Timestamp lastModified) {
        this.id = id;
        this.editorCode = editorCode;
        this.viewerCode = viewerCode;
        this.content = content;
        this.activeUsers = activeUsers;
        this.lastModified = lastModified;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String documentId) {
        this.id = documentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Cursor> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Map<String, Cursor> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public String getViewerCode(){return this.viewerCode;}
    public String getEditorCode(){return this.editorCode;}
    public Timestamp getLastModified(){return this.lastModified;}
    public void setLastModified(Timestamp lastModified){this.lastModified = lastModified; }
}