package com.tankgame.factory;

import com.tankgame.model.*;
import com.tankgame.strategy.*;

/**
 * Factory Pattern: Creates game objects
 */
public class GameObjectFactory {
    private static GameObjectFactory instance;
    
    private GameObjectFactory() {}
    
    public static GameObjectFactory getInstance() {
        if (instance == null) {
            instance = new GameObjectFactory();
        }
        return instance;
    }
    
    public Tank createPlayerTank(double x, double y) {
        return new Tank(x, y, Direction.UP, new PlayerMovementStrategy(), 3.5, true);
    }
    
    public Tank createEnemyTank(double x, double y, boolean aggressive) {
        MovementStrategy strategy = aggressive ? 
            new AggressiveMovementStrategy() : new RandomMovementStrategy();
        return new Tank(x, y, Direction.DOWN, strategy, 1.0, false);
    }
    
    public Wall createWall(double x, double y, double width, double height) {
        return new Wall(x, y, width, height);
    }
    
    public MedPack createMedPack(double x, double y) {
        return new MedPack(x, y);
    }
    
    public Explosion createExplosion(double x, double y) {
        return new Explosion(x, y);
    }
}

