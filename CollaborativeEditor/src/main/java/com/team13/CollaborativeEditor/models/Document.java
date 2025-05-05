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
    private Map<Integer, Stack<List<Operation>>> undoStacks = new HashMap<>();
    private Map<Integer, Stack<List<Operation>>> redoStacks = new HashMap<>();

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
        
        // Initialize undo/redo stacks for the new user
        undoStacks.putIfAbsent(user.getUserId(), new Stack<>());
        redoStacks.putIfAbsent(user.getUserId(), new Stack<>());
    }


    public void removeUser(int userId) {
        activeUsers.removeIf(user -> user.getUserId() == userId);
        undoStacks.remove(userId);
        redoStacks.remove(userId);
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

    public void addToHistory(List<Operation> operations, int userId) {
        // Initialize stacks for this user if they don't exist
        if (!undoStacks.containsKey(userId)) {
            undoStacks.put(userId, new Stack<>());
            redoStacks.put(userId, new Stack<>());
        }
        
        // Clear any redoable operations for this user
        while (!redoStacks.get(userId).empty()) {
            redoStacks.get(userId).pop();
        }
        
        undoStacks.get(userId).push(operations);
        System.out.println("Operations added to history for user " + userId + ": ");
        for (Operation op : operations) {
            System.out.println("Type: " + op.getType());
        }
    }

    public void undo(int userId) {
        if (!undoStacks.containsKey(userId) || undoStacks.get(userId).isEmpty()) {
            return;
        }

        List<Operation> operations = undoStacks.get(userId).pop();
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
        
        if (!redoStacks.containsKey(userId)) {
            redoStacks.put(userId, new Stack<>());
        }
        redoStacks.get(userId).push(newOperations);
    }

    public void redo(int userId) {
        if (!redoStacks.containsKey(userId) || redoStacks.get(userId).isEmpty()) {
            return;
        }

        List<Operation> operations = redoStacks.get(userId).pop();
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
        
        if (!undoStacks.containsKey(userId)) {
            undoStacks.put(userId, new Stack<>());
        }
        undoStacks.get(userId).push(newOperations);
    }

    // Add to Document class
    public boolean canUserEdit(String userId) {
        return authorizedUsers.contains(userId);
    }

    public void performOperation(List<Operation> ops, int userId) {
        if (!undoStacks.containsKey(userId)) {
            undoStacks.put(userId, new Stack<>());
            redoStacks.put(userId, new Stack<>());
        }
        undoStacks.get(userId).push(ops);
        redoStacks.get(userId).clear();
    }
}
