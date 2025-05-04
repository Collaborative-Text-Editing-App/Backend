package com.team13.CollaborativeEditor.controllers;

import com.team13.CollaborativeEditor.dto.*;
import com.team13.CollaborativeEditor.models.*;
import com.team13.CollaborativeEditor.services.*;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class EditorController {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/document.edit")
    @SendTo("/topic/document/{documentId}")
    public TextOperationMessage handleTextOperation(TextOperationMessage message) {
        Document doc = documentService.getDocument(message.getDocumentId());
        
        if (doc != null) {
            if ("INSERT".equals(message.getOperationType())) {
                Node parent = null;
                if (message.getPosition() > 0) {
                    parent = doc.getCrdt().findNodeAtPosition(message.getPosition() - 1);
                }
                documentService.insertCharacter(
                    message.getDocumentId(),
                    message.getUserId(),
                    message.getCharacter(),
                    parent
                );
            } else if ("DELETE".equals(message.getOperationType())) {
                Node nodeToDelete = doc.getCrdt().findNodeAtPosition(message.getPosition());
                if (nodeToDelete != null) {
                    documentService.deleteCharacter(
                        message.getDocumentId(),
                        message.getUserId(),
                        nodeToDelete
                    );
                }
            }
            
            // Send updated document content to all clients
            //broadcastDocumentUpdate(doc);
        }
        
        return message;
    }
    
    @MessageMapping("/cursor.update")
    @SendTo("/topic/document/{documentId}")
    public CursorUpdateMessage handleCursorUpdate(CursorUpdateMessage message) {
        userService.updateCursor(
            message.getUserId(),
            message.getPosition(),
            message.getDocumentId()
        );
        
        // Broadcast cursor update
        return message;
    }
    
//    private void broadcastDocumentUpdate(Document doc) {
//        DocumentUpdateMessage updateMsg = new DocumentUpdateMessage();
//        updateMsg.setDocumentId(doc.getId());
//        updateMsg.setContent(doc.getContent());
//        updateMsg.setCursors(doc.getActiveUsers().values().stream()
//            .collect(Collectors.toMap(User::getUserId, User::getCursor)));
//
//        messagingTemplate.convertAndSend("/topic/document/" + doc.getId(), updateMsg);
//    }
}