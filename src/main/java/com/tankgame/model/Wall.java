package com.tankgame.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Wall class representing indestructible obstacles
 */
public class Wall extends GameObject {
    private static final Color WALL_COLOR = Color.rgb(100, 100, 100);
    private static final Color WALL_BORDER = Color.rgb(70, 70, 70);
    
    public Wall(double x, double y, double width, double height) {
        super(x, y, width, height);
    }
    
    @Override
    public void update() {
        // Walls don't update
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Realistic concrete bunker wall
        // Base concrete color
        gc.setFill(Color.rgb(110, 105, 100));
        gc.fillRect(x, y, width, height);
        
        // Add concrete texture with cracks
        gc.setFill(Color.rgb(95, 90, 85));
        gc.fillRect(x + 5, y + 5, width - 10, 3);
        gc.fillRect(x + 3, y + height/2, width - 6, 2);
        
        // Weathering and dirt
        gc.setFill(Color.rgb(80, 75, 70));
        gc.fillRect(x + 2, y + height - 8, width - 4, 6);
        
        // 3D lighting effect - top highlight
        gc.setFill(Color.rgb(140, 135, 130, 0.7));
        gc.fillRect(x, y, width, 4);
        gc.fillRect(x, y, 4, height);
        
        // 3D shadow - bottom and right
        gc.setFill(Color.rgb(50, 45, 40, 0.8));
        gc.fillRect(x, y + height - 4, width, 4);
        gc.fillRect(x + width - 4, y, 4, height);
        
        // Rivets/bolts for military feel
        gc.setFill(Color.rgb(70, 65, 60));
        double rivetSize = 4;
        gc.fillOval(x + 5, y + 5, rivetSize, rivetSize);
        gc.fillOval(x + width - 9, y + 5, rivetSize, rivetSize);
        gc.fillOval(x + 5, y + height - 9, rivetSize, rivetSize);
        gc.fillOval(x + width - 9, y + height - 9, rivetSize, rivetSize);
        
        // Border
        gc.setStroke(Color.rgb(60, 55, 50));
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
    }
}

