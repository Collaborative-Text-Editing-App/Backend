package com.team13.CollaborativeEditor.controllers;

import com.team13.CollaborativeEditor.dto.*;
import com.team13.CollaborativeEditor.models.*;
import com.team13.CollaborativeEditor.services.*;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class EditorController {
    private static final String TEST_DOCUMENT_ID = "test-doc-123"; // Hardcoded ID for testing

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/document.edit")
    @SendTo("/topic/document/test-doc-123") // Hardcoded destination for testing
    public void handleTextOperation(TextOperationMessage message) {
        System.out.println("Received TextOperationMessage with character: " + message.getCharacter());
        Document doc = documentService.getDocument(TEST_DOCUMENT_ID);
        if (doc == null) {

            doc = documentService.createDocument("TESTING");
        }

        if (doc != null) {
            if ("INSERT".equals(message.getOperationType())) {
                Node parent = null;
                if (message.getPosition() > 0) {
                    parent = doc.getCrdt().findNodeAtPosition(message.getPosition() - 1);
                }
                documentService.insertCharacter(
                    TEST_DOCUMENT_ID,
                    message.getUserId(),
                    message.getCharacter(),
                    parent
                );
            } else if ("DELETE".equals(message.getOperationType())) {
                Node nodeToDelete = doc.getCrdt().findNodeAtPosition(message.getPosition());
                if (nodeToDelete != null) {
                    documentService.deleteCharacter(
                        doc.getId(),
                        message.getUserId(),
                        nodeToDelete
                    );
                }
            }
            
            // Send updated document content to all clients
            broadcastDocumentUpdate(doc);
        }
        
        return;
    }
    
    @MessageMapping("/cursor.update")
    @SendTo("/topic/document/test-doc-123") // Hardcoded destination for testing
    public CursorUpdateMessage handleCursorUpdate(CursorUpdateMessage message) {
        userService.updateCursor(
            message.getUserId(),
            message.getPosition(),
            message.getDocumentId()
        );
        
        // Broadcast cursor update
        return message;
    }

    @MessageMapping("/document/undo")
    public Document undo(TextOperationMessage message) {
        // Call your service to perform undo
        documentService.undo(message.getDocumentId());
        // Return the updated document or a status message
        broadcastDocumentUpdate(documentService.getDocument(message.getDocumentId()));
        return documentService.getDocument(message.getDocumentId());
    }

    @MessageMapping("/document/redo")
    public Document redo(TextOperationMessage message) {
        // Call your service to perform undo
        documentService.redo(message.getDocumentId());
        // Return the updated document or a status message
        broadcastDocumentUpdate(documentService.getDocument(message.getDocumentId()));
        return documentService.getDocument(message.getDocumentId());
    }
    
    private void broadcastDocumentUpdate(Document doc) {
        // print out document data for testing
        System.out.println("Document ID: " + doc.getId());
        System.out.println("Document title: " + doc.getTitle());
        System.out.println("Document content: " + doc.getContent());
        System.out.println("Document crdt: " + doc.getCrdt().getVisibleText());
        System.out.println("Document active users: " + doc.getActiveUsers());
        System.out.println("Document last modified: " + doc.getLastModified());
        System.out.println("Document editor code: " + doc.getEditorCode());
        System.out.println("Document viewer code: " + doc.getViewerCode());

        DocumentUpdateMessage updateMsg = new DocumentUpdateMessage();
        updateMsg.setDocumentId(doc.getId());
        updateMsg.setContent(doc.getCrdt().getVisibleText().toString());

//        updateMsg.setCursors(doc.getActiveUsers().values().stream()
//            .collect(Collectors.toMap(User::getUserId, User::getCursor)));
//
        messagingTemplate.convertAndSend("/topic/document/" + doc.getId(), updateMsg);
    }
}