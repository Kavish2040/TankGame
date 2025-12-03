package com.tankgame.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern: Manages game event notifications
 */
public class GameEventManager {
    private List<GameEventListener> listeners;
    
    public GameEventManager() {
        this.listeners = new ArrayList<>();
    }
    
    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }
    
    public void notifyListeners(GameEvent event, Object data) {
        for (GameEventListener listener : listeners) {
            listener.onGameEvent(event, data);
        }
    }
}

