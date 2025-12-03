package com.tankgame;

import javafx.application.Application;
import javafx.stage.Stage;
import com.tankgame.ui.GameWindow;

/**
 * Main entry point for the Tank War Game
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        GameWindow gameWindow = new GameWindow();
        gameWindow.start(primaryStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

