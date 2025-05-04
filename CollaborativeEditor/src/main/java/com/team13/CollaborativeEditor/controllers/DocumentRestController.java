package com.team13.CollaborativeEditor.controllers;

import com.team13.CollaborativeEditor.dto.DocumentUpdateMessage;
import com.team13.CollaborativeEditor.models.*;
import com.team13.CollaborativeEditor.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentRestController {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<DocumentUpdateMessage> createDocument() {
        DocumentUpdateMessage doc = documentService.createDocument();
        return ResponseEntity.ok(doc);
    }
    
    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocument(@PathVariable String id) {
        Document doc = documentService.getDocument(id);
        if (doc != null) {
            return ResponseEntity.ok(doc);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/join")
    public ResponseEntity<Document> joinByCode(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String username = payload.getOrDefault("username", "Anonymous");
        
        Document doc = documentService.getDocumentByCode(code);
        if (doc != null) {
            User user = userService.createUser(username, 
                code.equals(doc.getEditorCode()) ? "EDITOR" : "VIEWER");
            
            userService.addUserToDocument(user.getUserId(), doc.getId(), 
                code.equals(doc.getEditorCode()));
                
//            if (code.equals(doc.getEditorCode())) {
//                doc.authorizeUser(user.getUserId());
//            }
            
            //doc.addUser(user);
            
            return ResponseEntity.ok(doc);
        }
        
        return ResponseEntity.notFound().build();
    }
}