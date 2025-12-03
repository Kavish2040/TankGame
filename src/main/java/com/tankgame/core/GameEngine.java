package com.tankgame.core;

import com.tankgame.model.*;
import com.tankgame.factory.GameObjectFactory;
import com.tankgame.observer.*;
import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * Singleton Pattern: Core game engine managing game state and logic
 */
public class GameEngine {
    private static GameEngine instance;
    
    private static final double MAP_WIDTH = 1000;
    private static final double MAP_HEIGHT = 800;
    private static final int INITIAL_ENEMY_COUNT = 6;
    private static final int INITIAL_LIVES = 3;
    private static final double ENEMY_FIRE_RATE = 0.01; 
    
    private Tank playerTank;
    private List<Tank> enemyTanks;
    private List<Missile> missiles;
    private List<Wall> walls;
    private List<MedPack> medPacks;
    private List<Explosion> explosions;
    private List<GameObject> allObjects;
    
    private GameObjectFactory factory;
    private GameEventManager eventManager;
    
    private int score;
    private int lives;
    private GameState gameState;
    private Set<KeyCode> pressedKeys;
    
    public enum GameState {
        PLAYING, PAUSED, WON, LOST
    }
    
    private GameEngine() {
        factory = GameObjectFactory.getInstance();
        eventManager = new GameEventManager();
        pressedKeys = new HashSet<>();
        initializeGame();
    }
    
    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }
    
    public void initializeGame() {
        enemyTanks = new ArrayList<>();
        missiles = new ArrayList<>();
        walls = new ArrayList<>();
        medPacks = new ArrayList<>();
        explosions = new ArrayList<>();
        allObjects = new ArrayList<>();
        
        score = 0;
        lives = INITIAL_LIVES;
        gameState = GameState.PLAYING;
        
        // Create player tank
        playerTank = factory.createPlayerTank(MAP_WIDTH / 2, MAP_HEIGHT - 100);
        allObjects.add(playerTank);
        
        // Create enemy tanks
        createEnemyTanks();
        
        // Create walls
        createWalls();
        
        // Create med packs
        createMedPacks();
    }
    
    private void createEnemyTanks() {
        Random random = new Random();
        // Spread enemies across the entire map in OPEN areas (away from walls)
        // Positions carefully chosen to avoid wall collisions at spawn
        double[][] positions = {
            {150, 100},   // Top left - open area
            {500, 100},   // Top center - open area
            {850, 100},   // Top right - open area
            {200, 500},   // Middle left - open area
            {800, 500},   // Middle right - open area
            {500, 650}    // Bottom center - open area
        };
        
        for (int i = 0; i < INITIAL_ENEMY_COUNT; i++) {
            double x = positions[i][0];
            double y = positions[i][1];
            
            // Mix of random and aggressive AI (50/50 split)
            boolean aggressive = i % 2 == 0;
            Tank enemy = factory.createEnemyTank(x, y, aggressive);
            enemyTanks.add(enemy);
            allObjects.add(enemy);
        }
    }
    
    private void createWalls() {
        // Create a Battle City-style obstacle course
        double wallSize = 40;
        
        // Top row - protective barriers
        walls.add(factory.createWall(150, 150, wallSize * 2, wallSize));
        walls.add(factory.createWall(350, 150, wallSize * 2, wallSize));
        walls.add(factory.createWall(550, 150, wallSize * 2, wallSize));
        walls.add(factory.createWall(750, 150, wallSize * 2, wallSize));
        
        // Left side barriers
        walls.add(factory.createWall(100, 250, wallSize, wallSize * 3));
        walls.add(factory.createWall(100, 450, wallSize, wallSize * 2));
        
        // Right side barriers
        walls.add(factory.createWall(860, 250, wallSize, wallSize * 3));
        walls.add(factory.createWall(860, 450, wallSize, wallSize * 2));
        
        // Center maze structure
        walls.add(factory.createWall(250, 300, wallSize * 3, wallSize));
        walls.add(factory.createWall(650, 300, wallSize * 3, wallSize));
        
        walls.add(factory.createWall(450, 250, wallSize, wallSize * 4));
        
        // Middle horizontal barriers
        walls.add(factory.createWall(200, 450, wallSize * 2, wallSize));
        walls.add(factory.createWall(700, 450, wallSize * 2, wallSize));
        
        // Bottom protective barriers
        walls.add(factory.createWall(300, 600, wallSize * 2, wallSize));
        walls.add(factory.createWall(600, 600, wallSize * 2, wallSize));
        
        // Strategic cover points
        walls.add(factory.createWall(350, 500, wallSize, wallSize));
        walls.add(factory.createWall(600, 500, wallSize, wallSize));
        
        allObjects.addAll(walls);
    }
    
    private void createMedPacks() {
        // Spawn initial med packs using safe logic (avoiding walls)
        respawnMedPacks();
    }
    
    private void respawnMedPacks() {
        // Respawn med packs in different random locations (avoiding walls and other med packs)
        Random random = new Random();
        int numPacks = 5 + random.nextInt(3); // Spawn 5-7 med packs
        
        for (int i = 0; i < numPacks; i++) {
            boolean validPosition = false;
            double x = 0, y = 0;
            int attempts = 0;
            
            // Try to find a valid position (not on wall, not too close to other packs)
            while (!validPosition && attempts < 50) {
                x = 100 + random.nextDouble() * 800;
                y = 100 + random.nextDouble() * 600;
                
                // Create temporary med pack to check collision
                MedPack tempPack = factory.createMedPack(x, y);
                
                boolean collisionDetected = false;
                
                // Check collision with walls
                for (Wall wall : walls) {
                    if (tempPack.intersects(wall)) {
                        collisionDetected = true;
                        break;
                    }
                }
                
                // Check distance from other med packs (minimum 180 pixels for strategic gameplay)
                if (!collisionDetected) {
                    for (MedPack existingPack : medPacks) {
                        double distance = Math.sqrt(
                            Math.pow(tempPack.getX() - existingPack.getX(), 2) +
                            Math.pow(tempPack.getY() - existingPack.getY(), 2)
                        );
                        
                        // Ensure good spacing - hard to reach from one to another
                        if (distance < 180) {
                            collisionDetected = true;
                            break;
                        }
                    }
                }
                
                if (!collisionDetected) {
                    validPosition = true;
                }
                
                attempts++;
            }
            
            // Only add if we found a valid position
            if (validPosition) {
                MedPack medPack = factory.createMedPack(x, y);
                medPacks.add(medPack);
                allObjects.add(medPack);
            }
        }
        
        System.out.println("🎁 Med packs respawned! New count: " + medPacks.size());
    }
    
    public void update() {
        if (gameState != GameState.PLAYING) {
            return;
        }
        
        // Update player
        handlePlayerInput();
        playerTank.update();
        
        // Update enemies
        updateEnemyTanks();
        
        // Update missiles
        updateMissiles();
        
        // Update explosions
        updateExplosions();
        
        // Lambda & Stream: Update all med packs using forEach with method reference
        medPacks.forEach(MedPack::update);
        
        // Respawn med packs if all collected
        if (medPacks.isEmpty()) {
            respawnMedPacks();
        }
        
        // Check collisions
        checkCollisions();
        
        // Check win/lose conditions
        checkGameConditions();
    }
    
    private void handlePlayerInput() {
        if (!playerTank.isActive()) return;
        
        Direction moveDirection = null;
        
        if (pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.W)) {
            moveDirection = Direction.UP;
        } else if (pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S)) {
            moveDirection = Direction.DOWN;
        } else if (pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A)) {
            moveDirection = Direction.LEFT;
        } else if (pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D)) {
            moveDirection = Direction.RIGHT;
        }
        
        if (moveDirection != null) {
            playerTank.move(moveDirection, allObjects, MAP_WIDTH, MAP_HEIGHT);
        }
        
        if (pressedKeys.contains(KeyCode.SPACE)) {
            Missile missile = playerTank.fire();
            if (missile != null) {
                missiles.add(missile);
                allObjects.add(missile);
            }
        }
    }
    
    private void updateEnemyTanks() {
        Random random = new Random();
        
        for (Tank enemy : enemyTanks) {
            if (!enemy.isActive()) continue;
            
            // Get AI movement
            Direction aiDirection = enemy.getMovementStrategy().getNextMove(enemy, allObjects, playerTank);
            if (aiDirection != null) {
                enemy.move(aiDirection, allObjects, MAP_WIDTH, MAP_HEIGHT);
            }
            
          
            if (random.nextDouble() < ENEMY_FIRE_RATE) {
                Missile missile = enemy.fire();
                if (missile != null) {
                    missiles.add(missile);
                    allObjects.add(missile);
                }
            }
            
            enemy.update();
        }
    }
    
    private void updateMissiles() {
        // Lambda & Stream: Use removeIf with lambda expression for cleaner code
        missiles.removeIf(missile -> {
            if (!missile.isActive() || missile.isOutOfBounds(MAP_WIDTH, MAP_HEIGHT)) {
                missile.setActive(false);
                allObjects.remove(missile);
                return true; // Remove from list
            } else {
                missile.update();
                return false; // Keep in list
            }
        });
    }
    
    private void updateExplosions() {
        // Lambda & Stream: Cleaner iteration with forEach and removeIf
        explosions.forEach(Explosion::update); // Method reference for update
        
        explosions.removeIf(explosion -> {
            if (!explosion.isActive()) {
                allObjects.remove(explosion);
                return true;
            }
            return false;
        });
    }
    
    private void checkCollisions() {
        // Check missile collisions
        for (Missile missile : new ArrayList<>(missiles)) {
            if (!missile.isActive()) continue;
            
            // Check collision with walls
            for (Wall wall : walls) {
                if (missile.intersects(wall)) {
                    missile.setActive(false);
                    break;
                }
            }
            
            // Check collision with tanks
            if (missile.getOwner().isPlayer()) {
                // Player missile hitting enemies
                for (Tank enemy : enemyTanks) {
                    if (enemy.isActive() && missile.intersects(enemy)) {
                        enemy.takeDamage(missile.getDamage());
                        missile.setActive(false);
                        
                        if (!enemy.isActive()) {
                            createExplosion(enemy.getCenterX(), enemy.getCenterY());
                            score += 10;
                            eventManager.notifyListeners(GameEvent.ENEMY_DESTROYED, enemy);
                        }
                        break;
                    }
                }
            } else {
                // Enemy missile hitting player
                if (playerTank.isActive() && missile.intersects(playerTank)) {
                    playerTank.takeDamage(missile.getDamage());
                    missile.setActive(false);
                    
                    if (!playerTank.isActive()) {
                        createExplosion(playerTank.getCenterX(), playerTank.getCenterY());
                        lives--;
                        eventManager.notifyListeners(GameEvent.PLAYER_DESTROYED, playerTank);
                        
                        if (lives > 0) {
                            respawnPlayer();
                        }
                    }
                }
            }
        }
        
        // Check med pack collisions (with expanded collision area for easier pickup)
        Iterator<MedPack> medPackIterator = medPacks.iterator();
        while (medPackIterator.hasNext()) {
            MedPack medPack = medPackIterator.next();
            
            if (!medPack.isActive()) {
                continue;
            }
            
            // Check player collision (now using standard intersection since med packs aren't solid)
            if (playerTank.isActive() && playerTank.intersects(medPack)) {
                int oldHealth = playerTank.getHealth();
                playerTank.heal();
                System.out.println("Med pack collected! Health: " + oldHealth + " -> " + playerTank.getHealth());
                medPack.setActive(false);
                allObjects.remove(medPack);
                medPackIterator.remove();
                eventManager.notifyListeners(GameEvent.MEDPACK_COLLECTED, medPack);
                continue;
            }
            
            // Enemies can also collect med packs
            for (Tank enemy : enemyTanks) {
                if (enemy.isActive() && enemy.intersects(medPack)) {
                    enemy.heal();
                    medPack.setActive(false);
                    allObjects.remove(medPack);
                    medPackIterator.remove();
                    break;
                }
            }
        }
    }
    
    private void createExplosion(double x, double y) {
        Explosion explosion = factory.createExplosion(x, y);
        explosions.add(explosion);
        allObjects.add(explosion);
    }
    
    private void respawnPlayer() {
        playerTank = factory.createPlayerTank(MAP_WIDTH / 2, MAP_HEIGHT - 100);
        
        // Replace in allObjects
        allObjects.removeIf(obj -> obj instanceof Tank && ((Tank) obj).isPlayer());
        allObjects.add(playerTank);
    }
    
    private void checkGameConditions() {
        // Lambda & Stream: Check if all enemies are destroyed using functional approach
        boolean allEnemiesDestroyed = enemyTanks.stream()
                .noneMatch(Tank::isActive); // Method reference (shorthand lambda)
        
        if (allEnemiesDestroyed) {
            gameState = GameState.WON;
            eventManager.notifyListeners(GameEvent.GAME_WON, null);
        }
        
        // Check if player is out of lives
        if (lives <= 0 && !playerTank.isActive()) {
            gameState = GameState.LOST;
            eventManager.notifyListeners(GameEvent.GAME_LOST, null);
        }
    }
    
    public void keyPressed(KeyCode code) {
        pressedKeys.add(code);
        
        if (code == KeyCode.ESCAPE) {
            togglePause();
        }
        
        if (code == KeyCode.R && (gameState == GameState.WON || gameState == GameState.LOST)) {
            restart();
        }
    }
    
    public void keyReleased(KeyCode code) {
        pressedKeys.remove(code);
    }
    
    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }
    
    public void restart() {
        allObjects.clear();
        initializeGame();
    }
    
    // Getters
    public List<GameObject> getAllObjects() { return allObjects; }
    public Tank getPlayerTank() { return playerTank; }
    public List<Tank> getEnemyTanks() { return enemyTanks; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public GameState getGameState() { return gameState; }
    public double getMapWidth() { return MAP_WIDTH; }
    public double getMapHeight() { return MAP_HEIGHT; }
    public GameEventManager getEventManager() { return eventManager; }
}


