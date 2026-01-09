package checker;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Game {
    private Board gameBoard;
    private Pane gamePane;
    private Pane sidePane;

    

    // Game constructor
    public Game(Board gameBoard, Pane gamePane, Pane sidePane) {
        this.gameBoard = gameBoard;
        this.gamePane = gamePane;
        this.sidePane = sidePane; 
        
        
    }

    public void intializeGameBoard() {
        // Initialize the game board
        Color[] boardColors = {Color.BEIGE, Color.BROWN};
        Pierce[][] myPierces = new Pierce[10][10];
        this.gameBoard = new Board(this.gamePane, boardColors, myPierces);

    }












}
