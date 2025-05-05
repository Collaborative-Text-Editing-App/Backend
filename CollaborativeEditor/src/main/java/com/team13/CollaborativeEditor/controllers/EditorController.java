package com.team13.CollaborativeEditor.controllers;

import com.team13.CollaborativeEditor.dto.*;
import com.team13.CollaborativeEditor.models.*;
import com.team13.CollaborativeEditor.services.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EditorController {

    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/document.edit")
    public void handleTextOperation(TextOperationMessage message) {
        System.out.println("Received TextOperationMessage with character: " + message.getText());
        Document doc = documentService.getDocument(message.getDocumentId());

        if (doc != null) {
            if ("INSERT".equals(message.getOperationType())) {
                Node parent = null;
                if (message.getPosition() > 0) {
                    parent = doc.getCrdt().findNodeAtPosition(message.getPosition() - 1);
                }
                documentService.insertCharacter(
                    doc.getId(),
                    message.getUserId(),
                    message.getText(),
                    parent
                );
            } else if ("DELETE".equals(message.getOperationType())) {
                List<Node> nodesToDelete = new ArrayList<>();
                if (message.getTextLength() == 1) {
                    Node nodeToDelete = doc.getCrdt().findNodeAtPosition(message.getPosition());
                    nodesToDelete.add(nodeToDelete);
                    if (nodeToDelete != null) {
                        documentService.deleteCharacter(
                            doc.getId(),
                            message.getUserId(),
                            nodesToDelete
                        );
                    }
                } else {
                    System.out.println("Deleting " + message.getTextLength() + " characters at offset " + message.getPosition());
                    for (int i = 0; i < message.getTextLength(); i++) {
                        Node nodeToDelete = doc.getCrdt().findNodeAtPosition(message.getPosition() + i);
                        nodesToDelete.add(nodeToDelete);
                    }
                    documentService.deleteCharacter(
                        doc.getId(),
                        message.getUserId(),
                        nodesToDelete
                    );
                }

            }
            
            // Send updated document content to all clients
            broadcastDocumentUpdate(doc);
            // send update to users
        }
        
        return;
    }

    @MessageMapping("/cursor.update")
    public void handleCursorUpdate(CursorUpdateMessage message) {
        // Update the cursor position for this user on the server:
        Document doc = documentService.getDocument(message.getDocumentId());

        User targetUser = doc.getActiveUsers()
                .stream()
                .filter(user -> Objects.equals(user.getId(), message.getUserId()))
                .findFirst()
                .orElse(null);

        assert targetUser != null;
        Cursor cursor = targetUser.getCursor();
        cursor.setPosition(message.getPosition());
        targetUser.setCursor(cursor);


        activeUsersByDoc.computeIfAbsent(message.getDocumentId(), k -> new ArrayList<>());
        List<User> userList = activeUsersByDoc.get(message.getDocumentId());

        userList.removeIf(u -> Objects.equals(u.getId(), message.getUserId()));
        userList.add(targetUser);// Broadcast the updated list

         activeUsersByDoc.put(message.getDocumentId(), userList);

         UserUpdateMessage sendingMessage = new UserUpdateMessage();
         sendingMessage.setUsers(new ArrayList<>(userList)); // Defensive copy
         messagingTemplate.convertAndSend("/topic/users/" + message.getDocumentId(), sendingMessage);
    }

    @MessageMapping("/document/undo")
    public void undo(TextOperationMessage message) {
        // Convert userId string to integer
        int userId = Integer.parseInt(message.getUserId().hashCode() + "");
        // Call your service to perform undo
        documentService.undo(message.getDocumentId(), userId);
        // Return the updated document or a status message
        broadcastDocumentUpdate(documentService.getDocument(message.getDocumentId()));
    }

    @MessageMapping("/document/redo")
    public void redo(TextOperationMessage message) {
        // Convert userId string to integer
        int userId = Integer.parseInt(message.getUserId().hashCode() + "");
        // Call your service to perform redo
        documentService.redo(message.getDocumentId(), userId);
        // Return the updated document or a status message
        broadcastDocumentUpdate(documentService.getDocument(message.getDocumentId()));
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
        updateMsg.setId(doc.getId());
        updateMsg.setContent(doc.getCrdt().getVisibleText().toString());

//        updateMsg.setCursors(doc.getActiveUsers().values().stream()
//            .collect(Collectors.toMap(User::getUserId, User::getCursor)));
//
        messagingTemplate.convertAndSend("/topic/document/" + doc.getId(), updateMsg);
    }
    private final Map<String, List<User>> activeUsersByDoc = new ConcurrentHashMap<>();

    @MessageMapping("/join")
    public void handleJoin(UserJoinedMessage message) {
        // Add user to the active list
        String documentId = message.getDocumentId(); // Or extract from somewhere
        User user = message.getUser();
        activeUsersByDoc.computeIfAbsent(documentId, k -> new ArrayList<>()).add(user);

        // Broadcast the updated list
        UserUpdateMessage sendingMessage = new UserUpdateMessage();
        sendingMessage.setUsers(activeUsersByDoc.get(documentId));
        messagingTemplate.convertAndSend("/topic/users/" + documentId, sendingMessage);
    }

    @MessageMapping("/leave")
    public void handleLeave(UserJoinedMessage message) {
        // Add user to the active list
        String documentId = message.getDocumentId(); // Or extract from somewhere
        User user = message.getUser();
        activeUsersByDoc.computeIfAbsent(documentId, k -> new ArrayList<>())
                .removeIf(u -> u.getId().equals(user.getId()));

        // Broadcast the updated list
        UserUpdateMessage sendingMessage = new UserUpdateMessage();
        sendingMessage.setUsers(activeUsersByDoc.get(documentId));
        messagingTemplate.convertAndSend("/topic/users/" + documentId, sendingMessage);
    }

}