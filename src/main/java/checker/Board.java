package checker;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Board {
    // initialize instance variables
    private Pane myPane;
    private Pierce[][] myPierces;
    private Color[] boardColors;;

    // Board class constructor
    public Board(Pane myPane, Color[] boardColors, Pierce[][] myPierces) {
        this.myPane = myPane;
        this.boardColors = boardColors;
        this.myPierces = myPierces;
        this.setupBoard();
    }
    
     // setup the checkerboard on myPane
    private void setupBoard() {
        int squareSize = 30;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 == 0) {
                    Square square = new Square(this.boardColors[0], col * squareSize + 10, row * squareSize + 10);
                    this.myPane.getChildren().add(square.getSquare());
                } else {
                    Square square = new Square(this.boardColors[1], col * squareSize + 10, row * squareSize + 10);
                    this.myPane.getChildren().add(square.getSquare());
                }
            }
        }

    }   

    private void setupPierces() {
        // Method to setup initial pierce positions on the board
        
    }



}
