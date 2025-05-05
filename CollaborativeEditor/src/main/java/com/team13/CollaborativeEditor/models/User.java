package com.team13.CollaborativeEditor.models;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    private int userId;
    private UserRole role;
    private Cursor cursor;
    private boolean connected;
    private Timestamp lastSeen;

    public User(int id, UserRole role) {
        this.userId = id;
        this.role = role;
        this.cursor = new Cursor(0);
        this.connected = true;
        this.lastSeen = new Timestamp(System.currentTimeMillis());
    }

    public User() {
        // For serialization/deserialization
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
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
}
