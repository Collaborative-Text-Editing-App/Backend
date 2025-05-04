package com.team13.CollaborativeEditor.models;

import java.sql.Timestamp;
import java.util.*;

public class Document {
    private final String id;
    private final CRDT crdt;
    private final List<User> activeUsers;
    private final Timestamp createdAt;
    private Timestamp lastModified;
    private final String editorCode;
    private final String viewerCode;

    public Document() {
        this.id = UUID.randomUUID().toString();
        this.crdt = new CRDT(0); // System user
        this.activeUsers = new ArrayList<>();
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.lastModified = this.createdAt;
        this.editorCode = generateCode();
        this.viewerCode = generateCode();
    }

    public void addUser(User user) {
        // Replace existing user if already present
        activeUsers.removeIf(u -> u.getUserId() == user.getUserId());
        activeUsers.add(user);
    }

    public void removeUser(int userId) {
        activeUsers.removeIf(user -> user.getUserId() == userId);
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

    public List<User> getActiveUsers() {
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
