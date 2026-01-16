package checker;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class sideBar {
    private VBox pane;
    private Game game;
    private ToggleGroup group;
    private ComboBox<Difficulty> difficultyBox;
    private ComboBox<BoardTheme> themeBox;
    
    // Status Labels
    private Label turnLabel;
    private Label whiteCapturedLabel;
    private Label blackCapturedLabel;

    // sideBar class constructor
    public sideBar(Pane pane, Game game) {
        this.pane = (VBox) pane;
        this.game = game;
        this.setupControls();
    }
    
    // function that updates game state: whose turn it is and pieces captured by each player
    public void updateGameStats(String turnText, int whiteEaten, int blackEaten) {
        if (turnLabel != null) turnLabel.setText(turnText);
        if (whiteCapturedLabel != null) whiteCapturedLabel.setText("White Captured: " + whiteEaten);
        if (blackCapturedLabel != null) blackCapturedLabel.setText("Black Captured: " + blackEaten);
    }

    // setup and initialize sidebar controls and game-related settings
    private void setupControls() {
        this.pane.setAlignment(Pos.TOP_CENTER);
        this.pane.setSpacing(10);
        
        // --- Status Labels (Initialized here, added at bottom later) ---
        this.turnLabel = new Label("Turn: Black");
        this.turnLabel.getStyleClass().add("header-label");
        
        this.whiteCapturedLabel = new Label("White Captured: 0");
        this.whiteCapturedLabel.getStyleClass().add("normal-text");
        
        this.blackCapturedLabel = new Label("Black Captured: 0");
        this.blackCapturedLabel.getStyleClass().add("normal-text");
        
        // Label title setup
        Label title = new Label("Game Mode");
        title.getStyleClass().add("header-label");

        this.group = new ToggleGroup();

        RadioButton hvh = new RadioButton("Human vs Human");
        hvh.setToggleGroup(this.group);
        hvh.setSelected(true);

        RadioButton hvc = new RadioButton("Human vs Computer");
        hvc.setToggleGroup(this.group);
        
        // Difficulty controls
        Label diffLabel = new Label("Difficulty");
        diffLabel.getStyleClass().add("header-label"); // Use CSS
        
        this.difficultyBox = new ComboBox<>();
        this.difficultyBox.getItems().addAll(Difficulty.values());
        this.difficultyBox.setValue(Difficulty.EASY);
        this.difficultyBox.setDisable(true); // Disabled for HvH default
        this.difficultyBox.getStyleClass().add("game-combo-box");
        
        // Theme controls
        Label themeLabel = new Label("Board Theme");
        themeLabel.getStyleClass().add("header-label"); // Use CSS
        
        this.themeBox = new ComboBox<>();
        this.themeBox.getItems().addAll(BoardTheme.values());
        this.themeBox.setValue(BoardTheme.CLASSIC);
        this.themeBox.getStyleClass().add("game-combo-box");

        // settings confirmation button 
        Button confirmButton = new Button("Confirm Settings");
        confirmButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
        
        // restart game button
        Button startButton = new Button("Start New Game");
        startButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;");
        startButton.setDisable(true); // Initially disabled

        // add a quit   button 
        Button quitButton = new Button("Quit");
        quitButton.setStyle("-fx-background-color: #B22222; -fx-text-fill: white;");
        
        
        // event handlers for all the buttons buttons
        confirmButton.setOnAction(e -> {
             this.handleStartGame(); // Apply settings immediately
             startButton.setDisable(false);
             confirmButton.setDisable(true);
        });

        startButton.setOnAction(e -> {
            this.handleStartGame(); // Restart with current selection
            // Do not enable confirm button here; settings haven't changed
        });

        quitButton.setOnAction(e -> {
            System.exit(0);
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
        
        this.themeBox.valueProperty().addListener((obs, o, n) -> {
             startButton.setDisable(true);
             confirmButton.setDisable(false);
        });

        this.pane.getChildren().addAll(
            new Label(" "),
            this.turnLabel, 
            this.whiteCapturedLabel, 
            this.blackCapturedLabel,
            new javafx.scene.control.Separator(),
            title, 
            hvh, 
            hvc, 
            new Label(" "), 
            diffLabel, 
            this.difficultyBox,
            new Label(" "),
            themeLabel,
            this.themeBox,
            new Label(" "),
            confirmButton,
            startButton,
            quitButton
        );
    }
    
    // helper method that handles start new game option
    private void handleStartGame() {
        RadioButton selected = (RadioButton) this.group.getSelectedToggle();
        boolean vsComputer = selected.getText().equals("Human vs Computer");
        Difficulty selectedDiff = this.difficultyBox.getValue();
        BoardTheme selectedTheme = this.themeBox.getValue();
        this.game.resetGame(vsComputer, selectedDiff, selectedTheme);
    }
}

