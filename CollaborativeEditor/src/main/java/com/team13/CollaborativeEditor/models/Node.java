package com.team13.CollaborativeEditor.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String id;
    private int insertedBy;
    private char character;
    @JsonManagedReference
    private List<Node> children;
    @JsonBackReference
    private Node parent;
    private long timestamp;
    private boolean tombstone;

    public Node(String id, int insertedBy, char character) {
        this.id = id;
        this.insertedBy = insertedBy;
        this.character = character;
        this.children = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.tombstone = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public int getInsertedBy() { return insertedBy; }
    public char getCharacter() { return character; }
    public List<Node> getChildren() { return children; }
    public Node getParent() { return parent; }
    public long getTimestamp() { return timestamp; }
    public boolean isTombstone() { return tombstone; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setParent(Node parent) { this.parent = parent; }
    public void setTombstone(boolean tombstone) { this.tombstone = tombstone; }
}
