package com.tankgame.observer;

/**
 * Observer Pattern: Event types for game notifications
 */
public enum GameEvent {
    TANK_DESTROYED,
    ENEMY_DESTROYED,
    PLAYER_DESTROYED,
    MEDPACK_COLLECTED,
    GAME_WON,
    GAME_LOST
}

