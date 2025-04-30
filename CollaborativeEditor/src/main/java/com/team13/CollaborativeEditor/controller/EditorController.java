package com.team13.CollaborativeEditor.controller;

import com.team13.CollaborativeEditor.model.CRDT;
import com.team13.CollaborativeEditor.model.Node;
import com.team13.CollaborativeEditor.model.Operation;
import com.team13.CollaborativeEditor.model.OperationType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ConcurrentHashMap;

@Controller
public class EditorController {
    private final ConcurrentHashMap<String, CRDT> documents = new ConcurrentHashMap<>();

    @MessageMapping("/edit")
    @SendTo("/topic/document")
    public Operation handleEdit(Operation operation) {
        String documentId = String.valueOf(operation.getUserId());
        CRDT crdt = documents.computeIfAbsent(documentId, k -> new CRDT(operation.getUserId()));
        
        if (operation.getType() == OperationType.INSERT) {
            Node parent = operation.getNode().getParent();
            crdt.insert(parent, operation.getNode().getCharacter(), 
                       operation.getUserId(), operation.getTimestamp());
        } else if (operation.getType() == OperationType.DELETE) {
            crdt.delete(operation.getNode());
        }
        
        return operation;
    }

    @MessageMapping("/merge")
    @SendTo("/topic/document")
    public Node handleMerge(Node remoteParent, int userId) {
        String documentId = String.valueOf(userId);
        CRDT crdt = documents.computeIfAbsent(documentId, k -> new CRDT(userId));
        crdt.merge(remoteParent);
        return crdt.getParent();
    }
} 