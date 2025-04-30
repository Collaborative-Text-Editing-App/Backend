package com.team13.CollaborativeEditor.model;

import java.util.UUID;

public class CRDT {
    private Node parent; // root node marked with '#'
    private int userId;

    public CRDT(int userId) {
        this.userId = userId;
        this.parent = new Node(generateUniqueId(), -1, '#');
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public Node getParent() {
        return this.parent;
    }

    public Node insert(Node parent, char character, int insertedBy, long time) {
        Node actualParent = parent != null ? parent : this.parent;
        Node newNode = new Node(generateUniqueId(), insertedBy, character);
        newNode.setParent(actualParent);
        newNode.setTimestamp(time);

        int index = -1;
        for (int i = 0; i < actualParent.getChildren().size(); i++) {
            Node child = actualParent.getChildren().get(i);
            if (child.getTimestamp() < newNode.getTimestamp() ||
                (child.getTimestamp() == newNode.getTimestamp() && child.getUid() > newNode.getUid())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            actualParent.getChildren().add(newNode);
        } else {
            actualParent.getChildren().add(index, newNode);
        }
        return newNode;
    }

    public void delete(Node node) {
        node.setTombstone(true);
    }

    public String getText() {
        StringBuilder builder = new StringBuilder();
        for (Node child : parent.getChildren()) {
            builder.append(getTextHelper(child));
        }
        return builder.toString();
    }

    private String getTextHelper(Node node) {
        if (node.isTombstone()) return "";
        StringBuilder builder = new StringBuilder();
        builder.append(node.getCharacter());
        for (Node child : node.getChildren()) {
            builder.append(getTextHelper(child));
        }
        return builder.toString();
    }

    public void merge(Node remoteParent) {
        mergeNodes(parent, remoteParent);
    }

    private void mergeNodes(Node localNode, Node remoteNode) {
        for (Node remoteChild : remoteNode.getChildren()) {
            Node localChild = null;
            for (Node child : localNode.getChildren()) {
                if (child.getId().equals(remoteChild.getId())) {
                    localChild = child;
                    break;
                }
            }

            if (localChild == null) {
                Node newNode = insert(localNode, remoteChild.getCharacter(), remoteChild.getUid(), remoteChild.getTimestamp());
                newNode.setTombstone(remoteChild.isTombstone());
            } else {
                mergeNodes(localChild, remoteChild);
                if (remoteChild.isTombstone()) {
                    localChild.setTombstone(true);
                }
            }
        }
    }
} 