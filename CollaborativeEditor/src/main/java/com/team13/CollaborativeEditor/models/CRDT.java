package com.team13.CollaborativeEditor.models;

import java.util.UUID;

public class CRDT {
    private final Node root;
    private final int localUserId;

    public CRDT(int localUserId) {
        this.localUserId = localUserId;
        this.root = new Node(generateUniqueId(), -1, '#'); // Root is virtual
    }

    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public Node getRoot() {
        return root;
    }

    public Node insert(Node parent, char character, int insertedBy, long timestamp) {
        Node actualParent = parent != null ? parent : root;
        Node newNode = new Node(generateUniqueId(), insertedBy, character);
        newNode.setParent(actualParent);
        newNode.setTombstone(false);

        // If timestamp is not externally passed (e.g., for local insert)
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
        }
        newNode.setTimestamp(timestamp);
        newNode.setTombstone(false);
        actualParent.getChildren().add(findInsertIndex(actualParent, newNode), newNode);
        return newNode;
    }

    private int findInsertIndex(Node parent, Node newNode) {
        for (int i = 0; i < parent.getChildren().size(); i++) {
            Node existing = parent.getChildren().get(i);
            if (shouldInsertBefore(existing, newNode)) {
                return i;
            }
        }
        return parent.getChildren().size();
    }

    // Fix timestamp logic - higher timestamp should come after (later in document)
    private boolean shouldInsertBefore(Node a, Node b) {
        if (a.getTimestamp() != b.getTimestamp())
            return a.getTimestamp() < b.getTimestamp(); // Changed > to <
        return a.getInsertedBy() < b.getInsertedBy(); // Better tie-breaking with user IDs
    }

    public void delete(Node node) {
        if (node != null) {
            // If the node has children, move them up to the parent
            if (!node.getChildren().isEmpty()) {
                Node parent = node.getParent();
                if (parent != null) {
                    // Move all children to the parent
                    for (Node child : node.getChildren()) {
                        child.setParent(parent);
                        parent.getChildren().add(child);
                    }
                    // Clear the children list of the deleted node
                    node.getChildren().clear();
                }
            }
            node.setTombstone(true);
        }
    }

    public String getVisibleText() {
        StringBuilder sb = new StringBuilder();
        for (Node child : root.getChildren()) {
            sb.append(buildText(child));
        }
        return sb.toString();
    }

    private String buildText(Node node) {
        if (node.isTombstone()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(node.getCharacter());
        for (Node child : node.getChildren()) {
            sb.append(buildText(child));
        }
        return sb.toString();
    }

    public void merge(Node remoteRoot) {
        mergeNode(root, remoteRoot);
    }

    private void mergeNode(Node local, Node remote) {
        for (Node remoteChild : remote.getChildren()) {
            Node localMatch = local.getChildren().stream()
                .filter(child -> child.getId().equals(remoteChild.getId()))
                .findFirst().orElse(null);

            if (localMatch == null) {
                Node inserted = insert(local, remoteChild.getCharacter(), remoteChild.getInsertedBy(), remoteChild.getTimestamp());
                inserted.setTombstone(remoteChild.isTombstone());
            } else {
                if (remoteChild.isTombstone()) {
                    localMatch.setTombstone(true);
                }
                mergeNode(localMatch, remoteChild);
            }
        }
    }

    // Replace the incorrect node finding methods
    public Node findNodeAtPosition(int position) {
        if (position < 0) return null;
        int[] counter = new int[1]; // Use array to allow modification in recursive calls
        return findNodeAtPositionRecursive(root, position, counter);
    }

    private Node findNodeAtPositionRecursive(Node node, int targetPosition, int[] currentPos) {
        if (node == null) return null;
        
        // For non-root and visible nodes, check if this is our target position
        if (node != root && !node.isTombstone()) {
            if (currentPos[0] == targetPosition) {
                return node;
            }
            currentPos[0]++; // Increment position counter for each visible character
        }
        
        // Continue searching through children
        for (Node child : node.getChildren()) {
            Node result = findNodeAtPositionRecursive(child, targetPosition, currentPos);
            if (result != null) return result;
        }
        
        return null;
    }
}
