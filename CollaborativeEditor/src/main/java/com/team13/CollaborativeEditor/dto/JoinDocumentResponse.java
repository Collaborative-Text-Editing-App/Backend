package com.team13.CollaborativeEditor.dto;

import com.team13.CollaborativeEditor.models.UserRole;

public class JoinDocumentResponse {
    private DocumentUpdateMessage document;
    private UserRole role;

    public JoinDocumentResponse(DocumentUpdateMessage document, UserRole role) {
        this.document = document;
        this.role = role;
    }

    public DocumentUpdateMessage getDocument() { return document; }
    public UserRole getRole() { return role; }
    public void setDocument(DocumentUpdateMessage doc) { this.document = doc; }
    public void setRole(UserRole role) { this.role = role; }
}