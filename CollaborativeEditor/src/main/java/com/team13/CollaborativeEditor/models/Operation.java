package com.team13.CollaborativeEditor.models;

public class Operation {
    private OperationType type;
    private Node node;
    private int userId;
    private long timestamp;
    private Node parent;

    public Operation(OperationType type, Node node, int userId, long timestamp) {
        this.type = type;
        this.node = node;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public OperationType getType() {
        return type;
    }

    public Node getNode() {
        return node;
    }

    public int getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Node getParent() {
        return parent;
    }
} 