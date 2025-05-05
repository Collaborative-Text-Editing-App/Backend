package com.team13.CollaborativeEditor.services;

import com.team13.CollaborativeEditor.dto.DocumentUpdateMessage;
import com.team13.CollaborativeEditor.models.*;
import org.springframework.stereotype.Service;
import com.team13.CollaborativeEditor.models.User;
import com.team13.CollaborativeEditor.models.CRDT;

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
        User user = new User(doc.getActiveUsers().size(),  UserRole.EDITOR);
        doc.addUser(user);
        documents.put(doc.getId(), doc);
        return toDTO(doc);
    }

    public DocumentUpdateMessage importDocument(String content) {
        Document doc = new Document();
        User user = new User(doc.getActiveUsers().size(),  UserRole.EDITOR);
        doc.addUser(user);

        // Insert content into the CRDT character by character.
        CRDT crdt = doc.getCrdt();
        Node parent = crdt.getRoot();
        int insertedBy = 0;

        // We'll insert each char; parent advances for sequential insert.
        for (char c : content != null ? content.toCharArray() : new char[0]) {
            Node node = crdt.insert(parent, c, insertedBy, System.currentTimeMillis());
            parent = node; // move parent to newly inserted node
        }

        documents.put(doc.getId(), doc);
        return toDTO(doc);
    }

    public DocumentUpdateMessage toDTO(Document doc) {
        String content = doc.getCrdt().getVisibleText();
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
    
    public void insertCharacter(String documentId, String userId, String text, Node parent) {
        Document doc = getDocument(documentId);
        if (doc != null /*&& doc.isAuthorized(userId)*/) {
            List<Operation> operations = new ArrayList<>();
            if (text.length() == 1) {
                int userIdInt = Integer.parseInt(userId.hashCode() + "");
                Node node = doc.getCrdt().insert(parent, text.charAt(0), userIdInt, System.currentTimeMillis());
                doc.updateLastModified();
                operations.add(new Operation(OperationType.DELETE, node, userIdInt, System.currentTimeMillis()));
            } else {
                for (int i = 0; i < text.length(); i++) {
                    int userIdInt = Integer.parseInt(userId.hashCode() + "");
                    Node node = doc.getCrdt().insert(parent, text.charAt(i), userIdInt, System.currentTimeMillis());
                    doc.updateLastModified();
                    operations.add(new Operation(OperationType.DELETE, node, userIdInt, System.currentTimeMillis()));
                }
            }
            doc.addToHistory(operations);
            
            // Add operation to history for undo/redo
        }
    }
    
    public void deleteCharacter(String documentId, String userId, List<Node> nodes) {
        Document doc = getDocument(documentId);
        if (doc != null) {
            List<Operation> operations = new ArrayList<>();
            for (Node node : nodes) {
                doc.getCrdt().delete(node);
                doc.updateLastModified();
                int userIdInt = Integer.parseInt(userId.hashCode() + "");
                Operation op = new Operation(OperationType.INSERT, node, userIdInt, System.currentTimeMillis());
                operations.add(op); 
            }
            doc.addToHistory(operations);
        }
    }

    public void undo(String documentId) {
        Document doc = getDocument(documentId);
        if (doc != null) {
            doc.undo();
        }
    }

    public void redo(String documentId) {
        Document doc = getDocument(documentId);
        if (doc != null) {
            doc.redo();
        }
    }
}