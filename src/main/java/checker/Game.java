package checker;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Game {
    private Board gameBoard;
    private Pane gamePane;
    private Pane sidePane;

    

    // Game constructor
    public Game(Pane gamePane, Pane sidePane) {
        this.gamePane = gamePane;
        this.sidePane = sidePane;
        this.initializeGameBoard();
        
    }

    //initalize game board with all the pierces
    private void initializeGameBoard() {
        // Initialize the game board
        Color[] boardColors = {Color.BEIGE, Color.BROWN};
        this.gameBoard = new Board(this.gamePane, boardColors);

    }












}
