package com.tankgame.observer;

/**
 * Observer Pattern: Interface for game event listeners
 */
public interface GameEventListener {
    void onGameEvent(GameEvent event, Object data);
}

