package com.team13.CollaborativeEditor.dto;

import com.team13.CollaborativeEditor.models.User;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import com.team13.CollaborativeEditor.models.Cursor;
import com.team13.CollaborativeEditor.models.User;

public class DocumentUpdateMessage {
    private String id;
    private String editorCode;
    private String viewerCode;
    private String content;
    private List<User> activeUsers;
    private Timestamp lastModified;
    public DocumentUpdateMessage(){}

    public DocumentUpdateMessage(String id, String editorCode, String viewerCode, String content,
                                 List<User> activeUsers, Timestamp lastModified) {
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

    public List<User> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<User> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public String getViewerCode(){return this.viewerCode;}
    public String getEditorCode(){return this.editorCode;}
    public Timestamp getLastModified(){return this.lastModified;}
    public void setLastModified(Timestamp lastModified){this.lastModified = lastModified; }
}