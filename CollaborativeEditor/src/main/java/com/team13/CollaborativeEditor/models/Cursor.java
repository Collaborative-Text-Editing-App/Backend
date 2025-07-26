package com.team13.CollaborativeEditor.models;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Cursor {
    private int position;
    private String color;

    // Predefined list of colors
    private static final List<String> COLORS = Arrays.asList(
            "#FF5733", // Red-Orange
            "#33FF57", // Green
            "#3357FF", // Blue
            "#F39C12", // Yellow-Orange
            "#9B59B6", // Purple
            "#1ABC9C", // Teal
            "#E74C3C", // Red
            "#34495E"  // Dark Blue-Gray
    );

    private static final Random RANDOM = new Random();

    public Cursor(int position) {
        this.position = position;
        this.color = COLORS.get(RANDOM.nextInt(COLORS.size())); // Random color
    }

    // Getters and Setters
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
