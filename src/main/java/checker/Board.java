package checker;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Board {    
    // initialize instance variables
    private Pane myPane;
    private Pierce[][] myPierces;
    private Color[] boardColors;;

    // Board class constructor
    public Board(Pane myPane, Color[] boardColors) {

        this.myPane = myPane;
        this.boardColors = boardColors;
        this.myPierces = new Pierce[10][10];
        this.setupBoard();
        this.setupPierces();
    }
    
     // setup the checkerboard on myPane
    private void setupBoard() {
        int squareSize = 50;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 == 0) {
                    Square square = new Square(this.boardColors[0], col * squareSize + 20, row * squareSize + 20); // the additional 20 is for the offset values up and down
                    this.myPane.getChildren().add(square.getSquare());
                } else {
                    Square square = new Square(this.boardColors[1], col * squareSize + 20, row * squareSize + 20);
                    this.myPane.getChildren().add(square.getSquare());
                }

            }
        }

    }

    
    private void setupPierces() {
        int squareSize = 50;
        int boardOffset = 20;
        int centerOffset = 25; // to center the piece in the square

        // Loop through every square on the 10x10 board
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                
                // Only place pieces on "dark" squares (where row + col is odd)
                if ((row + col) % 2 != 0) {
                    
                    int xPos = (int) col * squareSize + boardOffset + centerOffset;
                    int yPos = (int) row * squareSize + boardOffset + centerOffset;

                    // Rows 0-3 are Black pieces
                    if (row < 4) {
                        Pierce blackPierce = new Pierce(Color.BLACK, xPos, yPos);
                        this.myPierces[row][col] = blackPierce;
                        this.myPane.getChildren().add(blackPierce.getCircle());
                    }
                    // Rows 6-9 are White pieces
                    else if (row > 5) {
                        Pierce whitePierce = new Pierce(Color.WHITE, xPos, yPos);
                        this.myPierces[row][col] = whitePierce;
                        this.myPane.getChildren().add(whitePierce.getCircle());
                    }
                }
            }
        }
    }

    public Pierce[][] getMyPierces() {
        return this.myPierces;
    }





}


