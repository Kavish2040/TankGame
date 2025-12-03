package com.tankgame.strategy;

import com.tankgame.model.Direction;
import com.tankgame.model.Tank;
import com.tankgame.model.GameObject;
import java.util.List;
import java.util.Random;

/**
 * Strategy Pattern: Random movement AI for enemy tanks
 * NEVER gets stuck - aggressive detection and instant direction changes
 */
public class RandomMovementStrategy implements MovementStrategy {
    private Random random = new Random();
    private Direction currentDirection;
    private int moveCounter = 0;
    private static final int MIN_MOVE_DURATION = 180; // 3 seconds minimum in one direction
    private static final int MAX_MOVE_DURATION = 300; // 5 seconds maximum
    
    private double lastX = -1;
    private double lastY = -1;
    private int stuckCounter = 0;
    private int consecutiveStuckFrames = 0;
    
    // Oscillation detection
    private Direction lastDirection;
    private int directionFlipCounter = 0;
    private int breakoutCounter = 0;
    
    public RandomMovementStrategy() {
        // Start with random direction
        currentDirection = Direction.values()[random.nextInt(4)];
        lastDirection = currentDirection;
    }
    
    @Override
    public Direction getNextMove(Tank tank, List<GameObject> obstacles, Tank playerTank) {
        // If breaking out of a loop, continue perpendicular move for a while
        if (breakoutCounter > 0) {
            breakoutCounter--;
            return currentDirection;
        }
        
        double currentX = tank.getX();
        double currentY = tank.getY();
        
        // AGGRESSIVE: Check if tank hasn't moved AT ALL
        if (lastX >= 0) {
            boolean notMoving = Math.abs(currentX - lastX) < 0.1 && Math.abs(currentY - lastY) < 0.1;
            
            if (notMoving) {
                consecutiveStuckFrames++;
                stuckCounter++;
                
                // INSTANT RESPONSE: After just 3 frames of not moving
                if (consecutiveStuckFrames >= 3) {
                    Direction previousDir = currentDirection;
                    
                    // Cycle through all 4 directions if needed
                    if (stuckCounter < 5) {
                        // Try opposite first
                        currentDirection = currentDirection.opposite();
                    } else if (stuckCounter < 10) {
                        // Try perpendicular
                        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                            currentDirection = random.nextBoolean() ? Direction.LEFT : Direction.RIGHT;
                        } else {
                            currentDirection = random.nextBoolean() ? Direction.UP : Direction.DOWN;
                        }
                    } else {
                        // Try random
                        currentDirection = Direction.values()[random.nextInt(4)];
                        stuckCounter = 0;
                    }
                    
                    // Check for oscillation (flipping back and forth)
                    if (currentDirection == lastDirection && currentDirection == previousDir.opposite()) {
                        directionFlipCounter++;
                    } else {
                        directionFlipCounter = 0;
                    }
                    
                    lastDirection = previousDir;
                    
                    // If flipping too much (stuck in loop), force perpendicular breakout
                    if (directionFlipCounter >= 2) {
                        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                            currentDirection = random.nextBoolean() ? Direction.LEFT : Direction.RIGHT;
                        } else {
                            currentDirection = random.nextBoolean() ? Direction.UP : Direction.DOWN;
                        }
                        breakoutCounter = 60; // Force this direction for 1 second
                        directionFlipCounter = 0;
                    }
                    
                    consecutiveStuckFrames = 0;
                    moveCounter = 0;
                }
            } else {
                // Tank is moving! Reset counters
                consecutiveStuckFrames = 0;
                stuckCounter = 0;
            }
        }
        
        lastX = currentX;
        lastY = currentY;
        
        moveCounter++;
        
        // Realistic movement: Change direction after moving successfully for a while
        if (consecutiveStuckFrames == 0 && moveCounter >= MIN_MOVE_DURATION) {
            if (moveCounter >= MAX_MOVE_DURATION || random.nextDouble() < 0.02) { // Lower random chance
                lastDirection = currentDirection;
                
                // Prefer turning 90 degrees (natural turn) over 180 (flip)
                if (random.nextDouble() < 0.8) {
                    // Turn perpendicular
                    if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                        currentDirection = random.nextBoolean() ? Direction.LEFT : Direction.RIGHT;
                    } else {
                        currentDirection = random.nextBoolean() ? Direction.UP : Direction.DOWN;
                    }
                } else {
                    // Random new direction
                    currentDirection = Direction.values()[random.nextInt(4)];
                }
                moveCounter = 0;
            }
        }
        
        return currentDirection;
    }
}

