package com.tankgame.strategy;

import com.tankgame.model.Direction;
import com.tankgame.model.Tank;
import com.tankgame.model.GameObject;
import java.util.List;

/**
 * Strategy Pattern: Player-controlled movement (no automatic movement)
 */
public class PlayerMovementStrategy implements MovementStrategy {
    
    @Override
    public Direction getNextMove(Tank tank, List<GameObject> obstacles, Tank playerTank) {
        // Player movement is controlled by keyboard input, not AI
        return null;
    }
}

