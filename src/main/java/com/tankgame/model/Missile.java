package com.tankgame.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.tankgame.util.ResourceManager;

/**
 * Missile class representing projectiles fired by tanks
 */
public class Missile extends GameObject {
    private static final double MISSILE_SIZE = 10;
    private static final double MISSILE_SPEED = 5;
    private static final int DAMAGE = 25;
    
    private Direction direction;
    private Tank owner;
    
    public Missile(double x, double y, Direction direction, Tank owner) {
        super(x, y, MISSILE_SIZE, MISSILE_SIZE);
        this.direction = direction;
        this.owner = owner;
    }
    
    @Override
    public void update() {
        x += direction.getDx() * MISSILE_SPEED;
        y += direction.getDy() * MISSILE_SPEED;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        Image missileImage = ResourceManager.getInstance().getMissileImage(direction);
        if (missileImage != null) {
            gc.drawImage(missileImage, x, y, width, height);
        }
    }
    
    public boolean isOutOfBounds(double mapWidth, double mapHeight) {
        return x < 0 || x > mapWidth || y < 0 || y > mapHeight;
    }
    
    public int getDamage() {
        return DAMAGE;
    }
    
    public Tank getOwner() {
        return owner;
    }
}

