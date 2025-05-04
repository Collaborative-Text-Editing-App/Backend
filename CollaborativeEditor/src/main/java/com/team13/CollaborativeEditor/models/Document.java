package com.team13.CollaborativeEditor.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Stack;

public class Document {
    private String id;
    private String title;
    private CRDT crdt;
    private Map<String, User> activeUsers;
    private List<String> authorizedUsers;
    private long createdAt;
    private long lastModified;
    private String editorCode;
    private String viewerCode;
    private List<Operation> history = new ArrayList<>();
    private int historyPointer = -1;
    private Stack<List<Operation>> undoStack = new Stack<>();
    private Stack<List<Operation>> redoStack = new Stack<>();

    public Document(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.crdt = new CRDT(0); // System user
        this.activeUsers = new HashMap<>();
        this.authorizedUsers = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.lastModified = this.createdAt;
        this.editorCode = generateCode();
        this.viewerCode = generateCode();
    }
    // temporary set id function
    public void setDocumentId(String id){
        this.id = id;
    }
    public void addUser(User user) {
        activeUsers.put(user.getUserId(), user);
    }

    public void removeUser(String userId) {
        activeUsers.remove(userId);
    }

    public void authorizeUser(String userId) {
        if (!authorizedUsers.contains(userId)) {
            authorizedUsers.add(userId);
        }
    }

    public boolean isAuthorized(String userId) {
        return authorizedUsers.contains(userId);
    }

    public void updateLastModified() {
        this.lastModified = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CRDT getCrdt() {
        return crdt;
    }

    public Map<String, User> getActiveUsers() {
        return activeUsers;
    }

    public List<String> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastModified() {
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
