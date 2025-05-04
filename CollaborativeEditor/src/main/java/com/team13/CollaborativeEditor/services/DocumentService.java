package com.team13.CollaborativeEditor.services;

import com.team13.CollaborativeEditor.dto.DocumentUpdateMessage;
import com.team13.CollaborativeEditor.models.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentService {
    // {"id": DocObject}
    private final Map<String, Document> documents = new ConcurrentHashMap<>();
    
    public DocumentUpdateMessage createDocument() {
        Document doc = new Document();
        documents.put(doc.getId(), doc);
        System.out.println("NEW DOCUMENT CREATED AT THE SERVICE");
        System.out.println(doc.getId());
        System.out.println(doc.getCreatedAt());
        System.out.println(doc.getEditorCode());
        System.out.println(doc.getViewerCode());

        return toDTO(doc);
    }

    public DocumentUpdateMessage toDTO(Document doc) {
        String content = doc.getCrdt().toPlainText(); // Convert CRDT to string for frontend
        Timestamp lastModified = doc.getLastModified();

        return new DocumentUpdateMessage(
                doc.getId(),
                doc.getEditorCode(),
                doc.getViewerCode(),
                content,
                doc.getActiveUsers(),
                lastModified
        );
    }
    
    public Document getDocument(String id) {
        return documents.get(id);
    }
    
    public Document getDocumentByCode(String code) {
        return documents.values().stream()
            .filter(doc -> doc.getEditorCode().equals(code) || doc.getViewerCode().equals(code))
            .findFirst().orElse(null);
    }
    
    public List<Document> getAllDocuments() {
        return new ArrayList<>(documents.values());
    }
    
    public void insertCharacter(String documentId, String userId, char character, Node parent) {
        Document doc = getDocument(documentId);
        if (doc != null) {
            int userIdInt = Integer.parseInt(userId.hashCode() + "");
            Node node = doc.getCrdt().insert(parent, character, userIdInt, System.currentTimeMillis());
            doc.updateLastModified();
            
            // Add operation to history for undo/redo
            Operation op = new Operation(OperationType.INSERT, node, userIdInt, System.currentTimeMillis());

        }
    }
    
    public void deleteCharacter(String documentId, String userId, Node node) {
        Document doc = getDocument(documentId);
        if (doc != null) {
            doc.getCrdt().delete(node);
            doc.updateLastModified();
            
            // Add operation to history for undo/redo
            int userIdInt = Integer.parseInt(userId.hashCode() + "");
            Operation op = new Operation(OperationType.DELETE, node, userIdInt, System.currentTimeMillis());
        }
    }
}