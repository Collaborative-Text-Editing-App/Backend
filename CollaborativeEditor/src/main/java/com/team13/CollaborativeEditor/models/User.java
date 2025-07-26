package com.team13.CollaborativeEditor.models;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private int userId;
    private String id;
    private UserRole role;
    private Cursor cursor;
    private Timestamp lastSeen;

    public User(int id, UserRole role) {
        this.userId = id;
        this.id = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        this.role = role;
        this.cursor = new Cursor(0);
        this.lastSeen = new Timestamp(System.currentTimeMillis());
    }

    public User() {
        // For serialization/deserialization
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.id = userId;
    }

    public String getId() {
        return id;
    }


    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public void updateLastSeen() {
        this.lastSeen = new Timestamp(System.currentTimeMillis());
    }
}
