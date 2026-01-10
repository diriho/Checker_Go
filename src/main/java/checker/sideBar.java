package checker;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class sideBar {
    private VBox pane;
    private Game game;
    private ToggleGroup group;
    private ComboBox<Difficulty> difficultyBox;

    public sideBar(Pane pane, Game game) {
        this.pane = (VBox) pane;
        this.game = game;
        this.setupControls();
    }

    private void setupControls() {
        this.pane.setAlignment(Pos.TOP_CENTER);
        this.pane.setSpacing(10);

        Label title = new Label("Game Mode");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        this.group = new ToggleGroup();

        RadioButton hvh = new RadioButton("Human vs Human");
        hvh.setToggleGroup(this.group);
        hvh.setSelected(true);

        RadioButton hvc = new RadioButton("Human vs Computer");
        hvc.setToggleGroup(this.group);
        
        // Difficulty controls
        Label diffLabel = new Label("Difficulty");
        diffLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        this.difficultyBox = new ComboBox<>();
        this.difficultyBox.getItems().addAll(Difficulty.values());
        this.difficultyBox.setValue(Difficulty.EASY);
        this.difficultyBox.setDisable(true); // Disabled for HvH default
        
        Button confirmButton = new Button("Confirm Settings");
        confirmButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
        
        Button startButton = new Button("Start New Game");
        startButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;");
        startButton.setDisable(true); // Initially disabled
        
        confirmButton.setOnAction(e -> {
             startButton.setDisable(false);
             confirmButton.setDisable(true);
             // Lock controls? Optional, but good feedback
             hvh.setDisable(true);
             hvc.setDisable(true);
             this.difficultyBox.setDisable(true);
        });

        startButton.setOnAction(e -> {
            this.handleStartGame();
            // Re-enable controls for next time
            confirmButton.setDisable(false);
            hvh.setDisable(false);
            hvc.setDisable(false);
            // Reset difficulty box enable state based on selection
            if (hvc.isSelected()) {
                this.difficultyBox.setDisable(false);
            }
            startButton.setDisable(true);
        });
        
        // Reset confirm if settings change
        this.group.selectedToggleProperty().addListener((obs, o, n) -> {
             startButton.setDisable(true);
             confirmButton.setDisable(false);
             if (n == hvc) {
                this.difficultyBox.setDisable(false);
            } else {
                this.difficultyBox.setDisable(true);
            }
        });
        
        this.difficultyBox.valueProperty().addListener((obs, o, n) -> {
             startButton.setDisable(true);
             confirmButton.setDisable(false);
        });

        this.pane.getChildren().addAll(
            new Label(" "), 
            title, 
            hvh, 
            hvc, 
            new Label(" "), 
            diffLabel, 
            this.difficultyBox,
            new Label(" "), 
            confirmButton,
            startButton
        );
    }
    
    private void handleStartGame() {
        RadioButton selected = (RadioButton) this.group.getSelectedToggle();
        boolean vsComputer = selected.getText().equals("Human vs Computer");
        Difficulty selectedDiff = this.difficultyBox.getValue();
        this.game.resetGame(vsComputer, selectedDiff);
    }
}

