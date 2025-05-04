package com.team13.CollaborativeEditor.services;

import com.team13.CollaborativeEditor.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentService {
    private final Map<String, Document> documents = new ConcurrentHashMap<>();
    
    public Document createDocument(String title) {
        Document doc = new Document(title);
        doc.setDocumentId("test-doc-123");
        documents.put("test-doc-123", doc);
        return doc;
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
        if (doc != null /*&& doc.isAuthorized(userId)*/) {
            int userIdInt = Integer.parseInt(userId.hashCode() + "");
            Node node = doc.getCrdt().insert(parent, character, userIdInt, System.currentTimeMillis());
            doc.updateLastModified();
            
            // Add operation to history for undo/redo
            Operation op = new Operation(OperationType.INSERT, node, userIdInt, System.currentTimeMillis());
            doc.addToHistory(op);
        }
    }
    
    public void deleteCharacter(String documentId, String userId, Node node) {
        Document doc = getDocument(documentId);
        if (doc != null /*&& doc.isAuthorized(userId)*/) {
            doc.getCrdt().delete(node);
            doc.updateLastModified();
            
            // Add operation to history for undo/redo
            int userIdInt = Integer.parseInt(userId.hashCode() + "");
            Operation op = new Operation(OperationType.DELETE, node, userIdInt, System.currentTimeMillis());
            doc.addToHistory(op);
        }
    }
}