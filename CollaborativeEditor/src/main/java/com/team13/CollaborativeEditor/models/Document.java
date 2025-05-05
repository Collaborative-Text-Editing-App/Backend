package com.team13.CollaborativeEditor.models;

import java.sql.Timestamp;
import java.util.*;
import java.util.Stack;

public class Document {
    private final String id;
    private final CRDT crdt;
    private final List<User> activeUsers;
    private final Timestamp createdAt;
    private Timestamp lastModified;
    private final String editorCode;
    private final String viewerCode;
    private String title;
    private List<String> authorizedUsers;
    private List<Operation> history = new ArrayList<>();
    private int historyPointer = -1;
    private Stack<List<Operation>> undoStack = new Stack<>();
    private Stack<List<Operation>> redoStack = new Stack<>();

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

    public String getTitle() {
        return title;
    }

    public void addToHistory(List<Operation> operations) {
        // Clear any redoable operations
        while (!redoStack.empty()) {
            redoStack.pop();
        }
        undoStack.push(operations);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            List<Operation> operations = undoStack.pop();
            List<Operation> newOperations = new ArrayList<>();
            for (Operation op : operations) {
                if (op.getType() == OperationType.DELETE) {
                    // apply inverse of op
                    getCrdt().delete(op.getNode());
                    updateLastModified();
                    Operation newop = new Operation(OperationType.INSERT, op.getNode(), op.getUserId(), System.currentTimeMillis());
                    newOperations.add(newop);
                }
                else if (op.getType() == OperationType.INSERT) {
                    Node node = op.getNode();
                    node.setTombstone(false);
                    updateLastModified();
                    Operation newop = new Operation(OperationType.DELETE, op.getNode(), op.getUserId(), System.currentTimeMillis());
                    newOperations.add(newop);
                }
            }
            redoStack.push(newOperations);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            List<Operation> operations = redoStack.pop();
            List<Operation> newOperations = new ArrayList<>();
            for (Operation op : operations) {
                if (op.getType() == OperationType.DELETE) {
                    // apply inverse of op
                    getCrdt().delete(op.getNode());
                    updateLastModified();
                    Operation newop = new Operation(OperationType.INSERT, op.getNode(), op.getUserId(), System.currentTimeMillis());
                    newOperations.add(newop);
                }
                else if (op.getType() == OperationType.INSERT) {
                    Node node = op.getNode();
                    node.setTombstone(false);
                    updateLastModified();
                    Operation newop = new Operation(OperationType.DELETE, op.getNode(), op.getUserId(), System.currentTimeMillis());
                    newOperations.add(newop);
                }
            }
            undoStack.push(newOperations);
        }
    }

    // Add to Document class
    public boolean canUserEdit(String userId) {
        return authorizedUsers.contains(userId);
    }

    public void performOperation(List<Operation> ops) {
        undoStack.push(ops);
        redoStack.clear();
        // apply op to document
    }
}
