package com.tankgame.model;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for all game objects
 */
public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected boolean active;
    
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = true;
    }
    
    public abstract void update();
    public abstract void render(GraphicsContext gc);
    
    public boolean intersects(GameObject other) {
        return this.x < other.x + other.width &&
               this.x + this.width > other.x &&
               this.y < other.y + other.height &&
               this.y + this.height > other.y;
    }
    
    // Getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public double getCenterX() { return x + width / 2; }
    public double getCenterY() { return y + height / 2; }
    
    public boolean isSolid() {
        return true;
    }
}

