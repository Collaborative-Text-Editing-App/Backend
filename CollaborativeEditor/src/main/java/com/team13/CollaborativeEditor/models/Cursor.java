package com.team13.CollaborativeEditor.models;

public class Cursor {
    private int position;   
    private String userId;  
    private String color;
    private String documentId;

    public Cursor(int position, String userId, String color) {
        this.position = position;
        this.userId = userId;
        this.color = color;
        this.documentId = null;
    }

    public Cursor(int position, String userId) {
        this(position, userId, null);
    }

    // Getters and Setters

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocument(String documentId) {
        this.documentId = documentId;
    }
}
