package checker;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

        Button startButton = new Button("Restart Game");
        startButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white;");
        startButton.setOnAction(e -> this.handleStartGame());

        this.pane.getChildren().addAll(new Label(" "), title, hvh, hvc, new Label(" "), startButton);
    }
    
    private void handleStartGame() {
        RadioButton selected = (RadioButton) this.group.getSelectedToggle();
        boolean vsComputer = selected.getText().equals("Human vs Computer");
        this.game.resetGame(vsComputer);
    }
}

