package com.team13.CollaborativeEditor.controllers;

import com.team13.CollaborativeEditor.dto.DocumentUpdateMessage;
import com.team13.CollaborativeEditor.dto.ImportDocumentRequest;
import com.team13.CollaborativeEditor.dto.JoinDocumentResponse;
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
    
    @PostMapping
    public ResponseEntity<DocumentUpdateMessage> createDocument() {
        DocumentUpdateMessage doc = documentService.createDocument();
        return ResponseEntity.ok(doc);
    }

    @PostMapping("/import")
    public ResponseEntity<DocumentUpdateMessage> importDocument(@RequestBody ImportDocumentRequest request) {
        DocumentUpdateMessage doc = documentService.importDocument(request.getContent());
        return ResponseEntity.ok(doc);
    }

    @GetMapping("/{code}")
    public ResponseEntity<JoinDocumentResponse> joinDocument(@PathVariable String code) {
        System.out.println(code);
        Document doc = documentService.getDocumentByCode(code);
        if (doc == null) return ResponseEntity.notFound().build();

        UserRole role = null;
        if (code.equals(doc.getEditorCode())) {
            role = UserRole.EDITOR;
        } else if (code.equals(doc.getViewerCode())) {
            role = UserRole.VIEWER;
        } else {
            return ResponseEntity.badRequest().build();
        }
        documentService.joinDocument(doc, role);
        DocumentUpdateMessage msg = documentService.toDTO(doc);
        JoinDocumentResponse response = new JoinDocumentResponse(msg, role);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }
    
    @PostMapping("/join")
    public ResponseEntity<Document> joinByCode(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String username = payload.getOrDefault("username", "Anonymous");
        
        Document doc = documentService.getDocumentByCode(code);
        if (doc != null) {
//            User user = userService.createUser(code.equals(doc.getEditorCode()) ? UserRole.EDITOR : UserRole.VIEWER);
//
//            userService.addUserToDocument(user.getUserId(), doc.getId(),
//                code.equals(doc.getEditorCode()));
//
//            if (code.equals(doc.getEditorCode())) {
//                doc.authorizeUser(user.getUserId());
//            }
            
            //doc.addUser(user);
            
            return ResponseEntity.ok(doc);
        }
        
        return ResponseEntity.notFound().build();
    }

    // @PostMapping("/api/document/undo")
    // public ResponseEntity<?> undo(@RequestParam String documentId, @RequestParam String userId) {
    //     documentService.undo(documentId, userId);
    //     return ResponseEntity.ok().build();
    // }

    // @PostMapping("/api/document/redo")
    // public ResponseEntity<?> redo(@RequestParam String documentId) {
    //     documentService.redo(documentId);
    //     return ResponseEntity.ok().build();
    // }
}