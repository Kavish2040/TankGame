package com.tankgame.strategy;

import com.tankgame.model.Direction;
import com.tankgame.model.Tank;
import com.tankgame.model.GameObject;
import java.util.List;
import java.util.Random;

/**
 * Strategy Pattern: Aggressive AI that tries to move toward the player
 * Smooth movement with periodic recalculation
 */
public class AggressiveMovementStrategy implements MovementStrategy {
    private Random random = new Random();
    private Direction currentDirection;
    private int moveCounter = 0;
    private static final int RECALCULATE_INTERVAL = 90; // Recalculate every 1.5 seconds
    private static final int MIN_DIRECTION_DURATION = 30; // Minimum frames before changing direction
    
    private double lastX = -1;
    private double lastY = -1;
    private int stuckCounter = 0;
    
    // Oscillation detection
    private Direction lastDirection;
    private int directionFlipCounter = 0;
    private int breakoutCounter = 0;
    
    public AggressiveMovementStrategy() {
        // Start facing down (toward player spawn area)
        currentDirection = Direction.DOWN;
        lastDirection = Direction.DOWN;
    }
    
    @Override
    public Direction getNextMove(Tank tank, List<GameObject> obstacles, Tank playerTank) {
        // Breakout logic
        if (breakoutCounter > 0) {
            breakoutCounter--;
            return currentDirection;
        }
        
        if (playerTank == null || !playerTank.isActive()) {
            // Random movement if no player
            if (moveCounter % 180 == 0) {
                currentDirection = Direction.values()[random.nextInt(4)];
            }
            moveCounter++;
            return currentDirection;
        }
        
        // AGGRESSIVE stuck detection
        double currentX = tank.getX();
        double currentY = tank.getY();
        
        if (lastX >= 0) {
            boolean notMoving = Math.abs(currentX - lastX) < 0.1 && Math.abs(currentY - lastY) < 0.1;
            
            if (notMoving) {
                stuckCounter++;
                
                // INSTANT: After just 3 frames, change direction!
                if (stuckCounter >= 3) {
                    Direction previousDir = currentDirection;
                    
                    if (stuckCounter < 6) {
                        // Try opposite
                        currentDirection = currentDirection.opposite();
                    } else if (stuckCounter < 9) {
                        // Try perpendicular
                        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                            currentDirection = random.nextBoolean() ? Direction.LEFT : Direction.RIGHT;
                        } else {
                            currentDirection = random.nextBoolean() ? Direction.UP : Direction.DOWN;
                        }
                    } else {
                        // Random
                        currentDirection = Direction.values()[random.nextInt(4)];
                        stuckCounter = 0;
                    }
                    
                    // Oscillation detection
                    if (currentDirection == lastDirection && currentDirection == previousDir.opposite()) {
                        directionFlipCounter++;
                    } else {
                        directionFlipCounter = 0;
                    }
                    lastDirection = previousDir;
                    
                    // Force breakout if looping
                    if (directionFlipCounter >= 2) {
                        if (currentDirection == Direction.UP || currentDirection == Direction.DOWN) {
                            currentDirection = random.nextBoolean() ? Direction.LEFT : Direction.RIGHT;
                        } else {
                            currentDirection = random.nextBoolean() ? Direction.UP : Direction.DOWN;
                        }
                        breakoutCounter = 45; // Breakout for 0.75s
                        directionFlipCounter = 0;
                    }
                    
                    moveCounter = 0;
                }
            } else {
                stuckCounter = 0;
            }
        }
        
        lastX = currentX;
        lastY = currentY;
        
        moveCounter++;
        
        // Realistic movement: Only recalculate if enough time has passed
        if (moveCounter >= RECALCULATE_INTERVAL && moveCounter >= MIN_DIRECTION_DURATION) {
            double dx = playerTank.getCenterX() - tank.getCenterX();
            double dy = playerTank.getCenterY() - tank.getCenterY();
            
            Direction newDirection = currentDirection;
            
            // 15% chance to move randomly for unpredictability
            if (random.nextDouble() < 0.15) {
                newDirection = Direction.values()[random.nextInt(4)];
            } else {
                // Move toward player but favor continuing current axis if aligned
                // This prevents "staircase" movement (zig-zagging)
                boolean xAligned = Math.abs(dx) < 20;
                boolean yAligned = Math.abs(dy) < 20;
                
                if (xAligned && !yAligned) {
                    newDirection = dy > 0 ? Direction.DOWN : Direction.UP;
                } else if (yAligned && !xAligned) {
                    newDirection = dx > 0 ? Direction.RIGHT : Direction.LEFT;
                } else {
                    // Standard chase logic
                    if (Math.abs(dx) > Math.abs(dy)) {
                        newDirection = dx > 0 ? Direction.RIGHT : Direction.LEFT;
                    } else {
                        newDirection = dy > 0 ? Direction.DOWN : Direction.UP;
                    }
                }
            }
            
            // Only change if direction is actually different (smoother movement)
            if (newDirection != currentDirection) {
                lastDirection = currentDirection;
                currentDirection = newDirection;
                moveCounter = 0;
            }
        }
        
        return currentDirection;
    }
}

