package com.tankgame.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.tankgame.strategy.MovementStrategy;
import com.tankgame.util.ResourceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Tank class representing both player and enemy tanks
 */
public class Tank extends GameObject {
    private static final double TANK_SIZE = 40;
    private static final int MAX_HEALTH = 100;
    private static final double FIRE_COOLDOWN = 0.5; // seconds
    
    private int health;
    private Direction direction;
    private MovementStrategy movementStrategy;
    private double speed;
    private boolean isPlayer;
    private double fireCooldown;
    private List<Missile> missiles;
    
    public Tank(double x, double y, Direction direction, MovementStrategy strategy, 
                double speed, boolean isPlayer) {
        super(x, y, TANK_SIZE, TANK_SIZE);
        this.health = MAX_HEALTH;
        this.direction = direction;
        this.movementStrategy = strategy;
        this.speed = speed;
        this.isPlayer = isPlayer;
        this.fireCooldown = 0;
        this.missiles = new ArrayList<>();
    }
    
    @Override
    public void update() {
        if (fireCooldown > 0) {
            fireCooldown -= 0.016; // Approximate frame time
        }
    }
    
    public void move(Direction newDirection, List<GameObject> obstacles, double mapWidth, double mapHeight) {
        if (newDirection != null) {
            this.direction = newDirection;
        }
        
        double newX = x + direction.getDx() * speed;
        double newY = y + direction.getDy() * speed;
        
        // Check boundaries
        if (newX < 0 || newX + width > mapWidth || newY < 0 || newY + height > mapHeight) {
            return;
        }
        
        // Check collisions with obstacles
        double oldX = x;
        double oldY = y;
        x = newX;
        y = newY;
        
        for (GameObject obstacle : obstacles) {
            if (obstacle != this && obstacle.isActive() && obstacle.isSolid() && this.intersects(obstacle)) {
                x = oldX;
                y = oldY;
                return;
            }
        }
    }
    
    public Missile fire() {
        if (fireCooldown > 0) {
            return null;
        }
        
        fireCooldown = FIRE_COOLDOWN;
        
        double missileX = x;
        double missileY = y;
        
        // Position missile at tank's front
        switch (direction) {
            case UP:
                missileX = x + width / 2 - 5;
                missileY = y - 10;
                break;
            case DOWN:
                missileX = x + width / 2 - 5;
                missileY = y + height;
                break;
            case LEFT:
                missileX = x - 10;
                missileY = y + height / 2 - 5;
                break;
            case RIGHT:
                missileX = x + width;
                missileY = y + height / 2 - 5;
                break;
        }
        
        return new Missile(missileX, missileY, direction, this);
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            active = false;
        }
    }
    
    public void heal() {
        health = MAX_HEALTH;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        Image tankImage = ResourceManager.getInstance().getTankImage(direction, isPlayer);
        if (tankImage != null) {
            gc.drawImage(tankImage, x, y, width, height);
            
            // Add color tint for player tank (blue) vs enemy tank (red)
            if (isPlayer) {
                gc.setFill(javafx.scene.paint.Color.rgb(0, 100, 255, 0.3));
                gc.fillRect(x, y, width, height);
            } else {
                gc.setFill(javafx.scene.paint.Color.rgb(255, 0, 0, 0.3));
                gc.fillRect(x, y, width, height);
            }
        }
        
        // Draw health bar above tank
        if (isPlayer || health < MAX_HEALTH) {
            drawHealthBar(gc);
        }
    }
    
    private void drawHealthBar(GraphicsContext gc) {
        double barWidth = width;
        double barHeight = 5;
        double barX = x;
        double barY = y - 10;
        
        // Background (red)
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(barX, barY, barWidth, barHeight);
        
        // Health (green)
        gc.setFill(javafx.scene.paint.Color.LIME);
        double healthWidth = barWidth * (health / (double) MAX_HEALTH);
        gc.fillRect(barX, barY, healthWidth, barHeight);
        
        // Border
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }
    
    // Getters
    public int getHealth() { return health; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public MovementStrategy getMovementStrategy() { return movementStrategy; }
    public boolean isPlayer() { return isPlayer; }
    public double getSpeed() { return speed; }
}

