package com.tankgame.model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import com.tankgame.util.ResourceManager;

/**
 * Explosion class for visual effects when tanks are destroyed
 */
public class Explosion extends GameObject {
    private static final double EXPLOSION_SIZE = 60;
    private static final double FRAME_DURATION = 0.05; // seconds per frame
    
    private int currentFrame;
    private double frameTimer;
    private int totalFrames;
    
    public Explosion(double x, double y) {
        super(x - EXPLOSION_SIZE / 2, y - EXPLOSION_SIZE / 2, EXPLOSION_SIZE, EXPLOSION_SIZE);
        this.currentFrame = 0;
        this.frameTimer = 0;
        this.totalFrames = 11; // 0.gif to 10.gif
    }
    
    @Override
    public void update() {
        frameTimer += 0.016; // Approximate frame time
        
        if (frameTimer >= FRAME_DURATION) {
            frameTimer = 0;
            currentFrame++;
            
            if (currentFrame >= totalFrames) {
                active = false;
            }
        }
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }
    
    @Override
    public void render(GraphicsContext gc) {
        if (currentFrame < totalFrames) {
            Image explosionImage = ResourceManager.getInstance().getExplosionImage(currentFrame);
            if (explosionImage != null) {
                gc.drawImage(explosionImage, x, y, width, height);
            }
        }
    }
}

