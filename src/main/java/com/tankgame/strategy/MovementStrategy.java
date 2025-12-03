package com.tankgame.strategy;

import com.tankgame.model.Direction;
import com.tankgame.model.Tank;
import com.tankgame.model.GameObject;
import java.util.List;

/**
 * Strategy Pattern: Interface for different tank movement behaviors
 */
public interface MovementStrategy {
    Direction getNextMove(Tank tank, List<GameObject> obstacles, Tank playerTank);
}

