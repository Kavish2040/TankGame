package com.tankgame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import com.tankgame.core.GameEngine;
import com.tankgame.model.Tank;

/**
 * Game UI components (HUD)
 */
public class GameUI {
    private GameEngine gameEngine;
    private Label scoreLabel;
    private Label livesLabel;
    private Label enemiesLabel;
    private ProgressBar healthBar;
    
    public GameUI(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }
    
    public VBox createTopPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(18));
        // Military-style dark UI with metallic gradient
        panel.setStyle(
            "-fx-background-color: linear-gradient(to bottom, " +
            "#1a1a1a 0%, #2d2d2d 50%, #1a1a1a 100%);" +
            "-fx-border-color: #4a4a4a;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 5px;" +
            "-fx-background-radius: 5px;"
        );
        
        // Top row: Score, Lives, Enemies with military styling
        HBox topRow = new HBox(50);
        topRow.setAlignment(Pos.CENTER);
        
        scoreLabel = createMilitaryLabel("⭐ SCORE: 0");
        livesLabel = createMilitaryLabel("❤️ LIVES: " + gameEngine.getLives());
        enemiesLabel = createMilitaryLabel("🎯 ENEMIES: " + gameEngine.getEnemyTanks().size());
        
        topRow.getChildren().addAll(scoreLabel, livesLabel, enemiesLabel);
        
        // Bottom row: Health bar with military styling
        HBox bottomRow = new HBox(15);
        bottomRow.setAlignment(Pos.CENTER);
        
        Label healthLabel = createMilitaryLabel("🛡️ ARMOR:");
        healthBar = new ProgressBar(1.0);
        healthBar.setPrefWidth(350);
        healthBar.setPrefHeight(25);
        // Realistic military green health bar
        healthBar.setStyle(
            "-fx-accent: linear-gradient(to right, #27ae60, #2ecc71);" +
            "-fx-background-color: #1a1a1a;" +
            "-fx-border-color: #4a4a4a;" +
            "-fx-border-width: 2px;"
        );
        
        bottomRow.getChildren().addAll(healthLabel, healthBar);
        
        panel.getChildren().addAll(topRow, bottomRow);
        return panel;
    }
    
    private Label createMilitaryLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        label.setTextFill(Color.rgb(0, 255, 100)); // Military green terminal color
        label.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.5);" +
            "-fx-padding: 8px 15px;" +
            "-fx-border-color: #00ff64;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 3px;" +
            "-fx-background-radius: 3px;"
        );
        return label;
    }
    
    public void update() {
        scoreLabel.setText("⭐ SCORE: " + gameEngine.getScore());
        livesLabel.setText("❤️ LIVES: " + gameEngine.getLives());
        
        // Count active enemies
        long activeEnemies = gameEngine.getEnemyTanks().stream()
            .filter(Tank::isActive)
            .count();
        enemiesLabel.setText("🎯 ENEMIES: " + activeEnemies);
        
        // Update health bar
        if (gameEngine.getPlayerTank() != null && gameEngine.getPlayerTank().isActive()) {
            double healthPercent = gameEngine.getPlayerTank().getHealth() / 100.0;
            healthBar.setProgress(healthPercent);
            
            // Change color based on health
            if (healthPercent > 0.6) {
                healthBar.setStyle("-fx-accent: #27ae60;"); // Green
            } else if (healthPercent > 0.3) {
                healthBar.setStyle("-fx-accent: #f39c12;"); // Orange
            } else {
                healthBar.setStyle("-fx-accent: #e74c3c;"); // Red
            }
        } else {
            healthBar.setProgress(0);
        }
    }
}

