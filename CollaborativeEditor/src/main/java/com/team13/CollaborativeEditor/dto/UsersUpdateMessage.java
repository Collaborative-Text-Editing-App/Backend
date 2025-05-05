package com.team13.CollaborativeEditor.dto;

import com.team13.CollaborativeEditor.models.User;

import java.util.List;

public class UsersUpdateMessage {
    private List<User> users;

    public UsersUpdateMessage() {} // Required for Jackson

    public UsersUpdateMessage(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}