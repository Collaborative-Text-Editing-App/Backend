package com.team13.CollaborativeEditor.services;

import com.team13.CollaborativeEditor.models.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
//    public User createUser(UserRole role) {
//        User user = new User(role);
//        users.put(user.getUserId(), user);
//        return user;
//    }
    
    public User getUser(String id) {
        return users.get(id);
    }
    
    public void updateCursor(String userId, int position, String documentId) {
        User user = getUser(userId);
        if (user != null) {
            Cursor cursor = user.getCursor();
            cursor.setPosition(position);
            user.updateLastSeen();
        }
    }
    
    public void addUserToDocument(String userId, String documentId, boolean isEditor) {
        User user = getUser(userId);
        if (user != null) {
//            user.addDocument(documentId);
//            user.getCursor().setDocument(documentId);
        }
    }
}