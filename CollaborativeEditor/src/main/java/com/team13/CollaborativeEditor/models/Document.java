package com.team13.CollaborativeEditor.models;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Stack;

public class Document {
    private final String id;
    private final CRDT crdt;
    private final Map<String, Cursor> activeUsers; // needs to be implemented
    private final Timestamp createdAt;
    private Timestamp lastModified;
    private final String editorCode;
    private final String viewerCode;
    private String title;
    private Stack<Operation> undoStack = new Stack<>();
    private Stack<Operation> redoStack = new Stack<>();

    public Document() {
        this.id = UUID.randomUUID().toString();
        this.crdt = new CRDT(0); // System user
        this.activeUsers = new HashMap<>();
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.lastModified = this.createdAt;
        this.editorCode = generateCode();
        this.viewerCode = generateCode();
    }

    public void addUser(Cursor cursor) {
        activeUsers.put(cursor.getUserId(), cursor);
    }


    public void removeUser(String userId) {
        activeUsers.remove(userId);
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

    public Map<String, Cursor> getActiveUsers() {
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
    public void addToHistory(Operation operation) {
        // Clear any redoable operations
        while (!redoStack.empty()) {
            redoStack.pop();
        }
        undoStack.push(operation);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Operation op = undoStack.pop();
            if (op.getType() == OperationType.DELETE) {
                // apply inverse of op
                getCrdt().delete(op.getNode());
                updateLastModified();
                Operation newop = new Operation(OperationType.INSERT, op.getNode(), op.getUserId(), System.currentTimeMillis());
                redoStack.push(newop);
            }
            else if (op.getType() == OperationType.INSERT) {
                Node node = op.getNode();
                node.setTombstone(false);
                updateLastModified();
                Operation newop = new Operation(OperationType.DELETE, op.getNode(), op.getUserId(), System.currentTimeMillis());
                redoStack.push(newop);
            }
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Operation op = redoStack.pop();
            if (op.getType() == OperationType.DELETE) {
                // apply inverse of op
                getCrdt().delete(op.getNode());
                updateLastModified();
                Operation newop = new Operation(OperationType.INSERT, op.getNode(), op.getUserId(), System.currentTimeMillis());
                undoStack.push(newop);
            }
            else if (op.getType() == OperationType.INSERT) {
                Node node = op.getNode();
                node.setTombstone(false);
                updateLastModified();
                Operation newop = new Operation(OperationType.DELETE, op.getNode(), op.getUserId(), System.currentTimeMillis());
                undoStack.push(newop);
            }
        }
    }

    public void performOperation(Operation op) {
        undoStack.push(op);
        redoStack.clear();
        // apply op to document
    }
}
