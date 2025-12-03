package com.tankgame.util;

import javafx.scene.image.Image;
import com.tankgame.model.Direction;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern: Manages game resources (images)
 */
public class ResourceManager {
    private static ResourceManager instance;
    private Map<String, Image> imageCache;
    
    private ResourceManager() {
        imageCache = new HashMap<>();
        loadImages();
    }
    
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
    
    private void loadImages() {
        try {
            // Load tank images
            loadImage("tankU", "images/tankU.gif");
            loadImage("tankD", "images/tankD.gif");
            loadImage("tankL", "images/tankL.gif");
            loadImage("tankR", "images/tankR.gif");
            
            // Load missile images
            loadImage("missileU", "images/missileU.gif");
            loadImage("missileD", "images/MissileD.gif");
            loadImage("missileL", "images/missileL.gif");
            loadImage("missileR", "images/missileR.gif");
            
            // Load explosion images
            for (int i = 0; i <= 10; i++) {
                loadImage("explosion" + i, "images/" + i + ".gif");
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }
    }
    
    private void loadImage(String key, String path) {
        try {
            String fullPath = "file:" + System.getProperty("user.dir") + "/" + path;
            Image image = new Image(fullPath);
            imageCache.put(key, image);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
        }
    }
    
    public Image getTankImage(Direction direction, boolean isPlayer) {
        String key = "tank" + getDirectionSuffix(direction);
        return imageCache.get(key);
    }
    
    public Image getMissileImage(Direction direction) {
        String key = "missile" + getDirectionSuffix(direction);
        return imageCache.get(key);
    }
    
    public Image getExplosionImage(int frame) {
        return imageCache.get("explosion" + frame);
    }
    
    private String getDirectionSuffix(Direction direction) {
        switch (direction) {
            case UP: return "U";
            case DOWN: return "D";
            case LEFT: return "L";
            case RIGHT: return "R";
            default: return "U";
        }
    }
}

