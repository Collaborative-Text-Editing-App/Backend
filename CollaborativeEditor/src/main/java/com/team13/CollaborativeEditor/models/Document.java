package com.team13.CollaborativeEditor.models;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Document {
    private final String id;
    private final CRDT crdt;
    private final Map<String, Cursor> activeUsers; // needs to be implemented 
    private final Timestamp createdAt;
    private Timestamp lastModified;
    private final String editorCode;
    private final String viewerCode;

    public Document() {
        this.id = UUID.randomUUID().toString();
        this.crdt = new CRDT(0); // System user
        this.activeUsers = new HashMap<>();
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.lastModified = this.createdAt;
        this.editorCode = generateCode();
        this.viewerCode = generateCode();
    }

    public void addUser(Cursor cursor) {
        activeUsers.put(cursor.getUserId(), cursor);
    }

    public void removeUser(String userId) {
        activeUsers.remove(userId);
    }


    public void updateLastModified() {
        this.lastModified = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public CRDT getCrdt() {
        return crdt;
    }

    public Map<String, Cursor> getActiveUsers() {
        return activeUsers;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Add getters
    public String getEditorCode() {
        return editorCode;
    }

    public String getViewerCode() {
        return viewerCode;
    }

    // Add to Document class
    public String getContent() {
        return this.crdt.getVisibleText();
    }
}
