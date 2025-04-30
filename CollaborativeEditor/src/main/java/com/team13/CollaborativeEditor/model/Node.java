package com.team13.CollaborativeEditor.model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String id;
    private int uid;
    private char character;
    private List<Node> children;
    private Node parent;
    private long timestamp;
    private boolean tombstone;

    public Node(String id, int uid, char character) {
        this.id = id;
        this.uid = uid;
        this.character = character;
        this.children = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
        this.tombstone = false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getUid() { return uid; }
    public void setUid(int uid) { this.uid = uid; }
    public char getCharacter() { return character; }
    public void setCharacter(char character) { this.character = character; }
    public List<Node> getChildren() { return children; }
    public void setChildren(List<Node> children) { this.children = children; }
    public Node getParent() { return parent; }
    public void setParent(Node parent) { this.parent = parent; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isTombstone() { return tombstone; }
    public void setTombstone(boolean tombstone) { this.tombstone = tombstone; }
} 