package com.team13.CollaborativeEditor.models;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private String userId;
    private String username;
    private String role;
    private Set<String> documentIds; // Changed to support multiple documents
    private Cursor cursor;
    private boolean connected;
    private Timestamp lastSeen;

    public User(String username, String role) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.role = role;
        this.documentIds = new HashSet<>();
        this.cursor = new Cursor(0, userId);
        this.connected = true;
        this.lastSeen = new Timestamp(System.currentTimeMillis());
    }

    public User() {
        // For serialization/deserialization
    }

    // Add document access
    public void addDocument(String documentId) {
        this.documentIds.add(documentId);
    }

    // Check document access
    public boolean hasAccessToDocument(String documentId) {
        return this.documentIds.contains(documentId);
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<String> getDocumentIds() {
        return documentIds;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public void updateLastSeen() {
        this.lastSeen = new Timestamp(System.currentTimeMillis());
    }

    public void setActiveDocument(String documentId) {
        this.addDocument(documentId);
        if (this.cursor != null) {
            this.cursor.setDocument(documentId);
        }
    }
}
