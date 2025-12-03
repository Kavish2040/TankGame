package com.tankgame.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * MedPack class representing health restoration items
 */
public class MedPack extends GameObject {
    private static final double MEDPACK_SIZE = 30;
    private static final Color MEDPACK_COLOR = Color.rgb(255, 100, 100);
    private static final Color CROSS_COLOR = Color.WHITE;
    
    private double pulseTimer = 0;
    
    public MedPack(double x, double y) {
        super(x, y, MEDPACK_SIZE, MEDPACK_SIZE);
    }
    
    @Override
    public void update() {
        pulseTimer += 0.05;
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        // Simple, clean pulsing effect
        double scale = 1.0 + Math.sin(pulseTimer) * 0.12;
        double scaledSize = MEDPACK_SIZE * scale;
        double offset = (MEDPACK_SIZE - scaledSize) / 2;
        
        double centerX = x + MEDPACK_SIZE / 2;
        double centerY = y + MEDPACK_SIZE / 2;
        
        // Soft glowing circle background
        gc.setFill(Color.rgb(255, 100, 100, 0.4));
        gc.fillOval(x - 8, y - 8, MEDPACK_SIZE + 16, MEDPACK_SIZE + 16);
        
        // Main circle background (clean white)
        gc.setFill(Color.WHITE);
        gc.fillOval(x + offset, y + offset, scaledSize, scaledSize);
        
        // Red cross (simple and clean)
        gc.setFill(Color.rgb(220, 50, 50));
        double crossThickness = scaledSize * 0.25;
        double crossLength = scaledSize * 0.65;
        
        // Horizontal bar
        gc.fillRoundRect(centerX - crossLength / 2, centerY - crossThickness / 2, 
                   crossLength, crossThickness, 3, 3);
        // Vertical bar
        gc.fillRoundRect(centerX - crossThickness / 2, centerY - crossLength / 2, 
                   crossThickness, crossLength, 3, 3);
        
        // Clean border
        gc.setStroke(Color.rgb(220, 50, 50));
        gc.setLineWidth(2.5);
        gc.strokeOval(x + offset, y + offset, scaledSize, scaledSize);
        
        // Simple "+HP" label above
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 12));
        gc.setStroke(Color.rgb(220, 50, 50));
        gc.setLineWidth(2);
        gc.strokeText("+HP", x + 3, y - 5);
        gc.fillText("+HP", x + 3, y - 5);
    }
}

