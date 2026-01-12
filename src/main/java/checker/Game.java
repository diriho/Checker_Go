package checker;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

// Game class to manage the high-level logic of the game state
public class Game {
    private Board gameBoard;
    private Pane gamePane;
    private Pane sidePane;
    private HumanPlayer player1;
    private HumanPlayer player2;

    

    // Game constructor
    public Game(Pane gamePane, Pane sidePane) {
        this.gamePane = gamePane;
        this.sidePane = sidePane;
        new sideBar(this.sidePane, this);
        this.resetGame(false, Difficulty.EASY, BoardTheme.CLASSIC); // Default to Human vs Human
    }

    public void resetGame(boolean vsComputer, Difficulty difficulty) {
        // Overload for backward compatibility or default theme
        this.resetGame(vsComputer, difficulty, BoardTheme.CLASSIC);
    }
    
    public void resetGame(boolean vsComputer, Difficulty difficulty, BoardTheme theme) {
        this.gamePane.getChildren().clear();
        this.initializeGameBoard(theme);
        this.initializePlayers(vsComputer, difficulty);
    }

    private void initializePlayers(boolean vsComputer, Difficulty difficulty) {
        // Player 1: Black (Moves Down)
        this.player1 = new HumanPlayer(this.gameBoard, Color.BLACK);
        
        // Player 2: White (Moves Up)
        if (vsComputer) {
             this.player2 = new ComputerPlayer(this.gameBoard, Color.WHITE, difficulty);
        } else {
             this.player2 = new HumanPlayer(this.gameBoard, Color.WHITE);
        }

        // Setup turn switching
        this.player1.setOnTurnEnd(() -> {
            this.player1.setTurn(false);
            this.player2.setTurn(true);
            // System.out.println("White's Turn");
        });

        this.player2.setOnTurnEnd(() -> {
            this.player2.setTurn(false);
            this.player1.setTurn(true);
            // System.out.println("Black's Turn");
        });

        // Start with Player 1 (Black)
        this.player1.setTurn(true);
        this.player2.setTurn(false);
        // System.out.println("Game Start: Black's Turn");
    }

    //initalize game board with all the pierces
    private void initializeGameBoard(BoardTheme theme) {
        // Initialize the game board
        Color[] boardColors = (theme != null) ? theme.getColors() : BoardTheme.CLASSIC.getColors();
        this.gameBoard = new Board(this.gamePane, boardColors);

    }












}
