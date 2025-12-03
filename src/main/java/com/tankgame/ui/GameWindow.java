package com.tankgame.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.tankgame.core.GameEngine;
import com.tankgame.model.GameObject;
import com.tankgame.observer.GameEvent;
import com.tankgame.observer.GameEventListener;

/**
 * Main game window with JavaFX rendering
 */
public class GameWindow implements GameEventListener {
    private static final double WINDOW_WIDTH = 1000;
    private static final double WINDOW_HEIGHT = 900;
    
    private Stage stage;
    private Canvas canvas;
    private GraphicsContext gc;
    private GameEngine gameEngine;
    private GameUI gameUI;
    
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.gameEngine = GameEngine.getInstance();
        
        // Register as event listener
        gameEngine.getEventManager().addListener(this);
        
        // Setup UI
        BorderPane root = new BorderPane();
        
        // Game canvas
        canvas = new Canvas(WINDOW_WIDTH, gameEngine.getMapHeight());
        gc = canvas.getGraphicsContext2D();
        
        // Game UI (HUD)
        gameUI = new GameUI(gameEngine);
        VBox topPanel = gameUI.createTopPanel();
        
        root.setTop(topPanel);
        root.setCenter(canvas);
        
        // Setup scene
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setFill(Color.BLACK);
        
        // Make canvas focusable
        canvas.setFocusTraversable(true);
        
        // Input handling on both scene and canvas
        scene.setOnKeyPressed(e -> {
            gameEngine.keyPressed(e.getCode());
            e.consume();
        });
        scene.setOnKeyReleased(e -> {
            gameEngine.keyReleased(e.getCode());
            e.consume();
        });
        
        canvas.setOnKeyPressed(e -> {
            gameEngine.keyPressed(e.getCode());
            e.consume();
        });
        canvas.setOnKeyReleased(e -> {
            gameEngine.keyReleased(e.getCode());
            e.consume();
        });
        
        // Setup stage
        stage.setTitle("Tank War Game");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
        stage.show();
        
        // Request focus for keyboard input
        canvas.requestFocus();
        
        // Start game loop
        startGameLoop();
    }
    
    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameEngine.update();
                render();
                gameUI.update();
            }
        };
        gameLoop.start();
    }
    
    private void render() {
        // Clear canvas with realistic battlefield background
        drawBackground();
        
        // Draw grid pattern (subtle)
        drawGrid();
        
        // Render all game objects
        for (GameObject obj : gameEngine.getAllObjects()) {
            if (obj.isActive()) {
                obj.render(gc);
            }
        }
        
        // Draw overlays based on game state
        switch (gameEngine.getGameState()) {
            case PAUSED:
                drawPauseOverlay();
                break;
            case WON:
                drawWinOverlay();
                break;
            case LOST:
                drawLoseOverlay();
                break;
        }
    }
    
    private void drawBackground() {
        // Realistic battlefield ground texture
        gc.setFill(Color.rgb(60, 70, 50)); // Dark olive green
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Add texture with random dirt patches
        gc.setFill(Color.rgb(50, 60, 45));
        for (int i = 0; i < 50; i++) {
            double x = (i * 137) % canvas.getWidth();
            double y = (i * 193) % canvas.getHeight();
            gc.fillOval(x, y, 20, 15);
        }
        
        // Darker patches for depth
        gc.setFill(Color.rgb(45, 55, 40));
        for (int i = 0; i < 30; i++) {
            double x = (i * 211) % canvas.getWidth();
            double y = (i * 157) % canvas.getHeight();
            gc.fillRect(x, y, 30, 25);
        }
    }
    
    private void drawGrid() {
        // Subtle grid for tactical feel
        gc.setStroke(Color.rgb(70, 80, 55, 0.3)); // Semi-transparent
        gc.setLineWidth(0.5);
        
        // Vertical lines
        for (int x = 0; x < canvas.getWidth(); x += 50) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }
        
        // Horizontal lines
        for (int y = 0; y < canvas.getHeight(); y += 50) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
    }
    
    private void drawPauseOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.7));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        gc.fillText("PAUSED", canvas.getWidth() / 2 - 120, canvas.getHeight() / 2);
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        gc.fillText("Press ESC to resume", canvas.getWidth() / 2 - 100, canvas.getHeight() / 2 + 50);
    }
    
    private void drawWinOverlay() {
        gc.setFill(Color.rgb(0, 100, 0, 0.8));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        gc.setFill(Color.YELLOW);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 70));
        gc.fillText("VICTORY!", canvas.getWidth() / 2 - 150, canvas.getHeight() / 2 - 50);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gc.fillText("Score: " + gameEngine.getScore(), canvas.getWidth() / 2 - 70, canvas.getHeight() / 2 + 20);
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        gc.fillText("Press R to restart", canvas.getWidth() / 2 - 90, canvas.getHeight() / 2 + 70);
    }
    
    private void drawLoseOverlay() {
        gc.setFill(Color.rgb(100, 0, 0, 0.8));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 70));
        gc.fillText("GAME OVER", canvas.getWidth() / 2 - 200, canvas.getHeight() / 2 - 50);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gc.fillText("Score: " + gameEngine.getScore(), canvas.getWidth() / 2 - 70, canvas.getHeight() / 2 + 20);
        
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        gc.fillText("Press R to restart", canvas.getWidth() / 2 - 90, canvas.getHeight() / 2 + 70);
    }
    
    @Override
    public void onGameEvent(GameEvent event, Object data) {
        // Handle game events (could add sound effects here)
        switch (event) {
            case ENEMY_DESTROYED:
                System.out.println("Enemy destroyed!");
                break;
            case PLAYER_DESTROYED:
                System.out.println("Player destroyed!");
                break;
            case MEDPACK_COLLECTED:
                System.out.println("Med pack collected!");
                break;
            case GAME_WON:
                System.out.println("Game won!");
                break;
            case GAME_LOST:
                System.out.println("Game lost!");
                break;
        }
    }
}

