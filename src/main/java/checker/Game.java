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
    
    // Check for Game Over conditions
    public void onTurnComplete(Color activeColor) {
        if (checkGameOver(activeColor)) {
            // Stop game
            this.player1.setTurn(false);
            this.player2.setTurn(false);
        }
    }

    private boolean checkGameOver(Color activeColor) {
        VirtualBoard vb = new VirtualBoard(this.gameBoard);
        boolean isWhite = (activeColor == Color.WHITE);
        byte activeByte = isWhite ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN;
        
        // 1. Check if active player has pieces
        int myPieces = vb.getPieceCount(isWhite);
        if (myPieces == 0) {
            // Opponent captured all pieces -> active player (who has 0) loses
            displayWin(isWhite ? Color.BLACK : Color.WHITE);
            return true;
        }

        // 2. Check if active player has legal moves
        boolean canMove = !vb.getLegalMoves(activeByte).isEmpty();
        if (!canMove) {
            // Have pieces but no moves -> Draw
            displayDraw();
            return true;
        }
        
        return false;
    }
    
    private void displayWin(Color winner) {
        javafx.scene.control.Label winMsg = new javafx.scene.control.Label(winner == Color.WHITE ? "White Wins!" : "Black Wins!");
        winMsg.setStyle("-fx-font-size: 40px; -fx-text-fill: red; -fx-background-color: rgba(255,255,255,0.8); -fx-padding: 20px;");
        // Center it
        winMsg.setTranslateX(150);
        winMsg.setTranslateY(200);
        this.gamePane.getChildren().add(winMsg);
        
        // Record Stats for Human Player (Assuming Human is Black/Player 1)
        // If Human vs Human, tracking is ambiguous without full auth system.
        // Assuming Player 1 (Black) is the "Main User"
        checker.data.UserDataManager dm = checker.data.UserDataManager.getInstance();
        if (winner == Color.BLACK) {
             dm.recordGame(true);
        } else {
             // Only record loss if Human lost
             dm.recordGame(false);
        }
    }

    private void displayDraw() {
        javafx.scene.control.Label drawMsg = new javafx.scene.control.Label("Draw!");
        drawMsg.setStyle("-fx-font-size: 40px; -fx-text-fill: blue; -fx-background-color: rgba(255,255,255,0.8); -fx-padding: 20px;");
        drawMsg.setTranslateX(200);
        drawMsg.setTranslateY(200);
        this.gamePane.getChildren().add(drawMsg);
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
            this.onTurnComplete(Color.WHITE); // Check status for White
        });

        this.player2.setOnTurnEnd(() -> {
            this.player2.setTurn(false);
            this.player1.setTurn(true);
            this.onTurnComplete(Color.BLACK); // Check status for Black
        });

        // Start with Player 1 (Black)
        this.player1.setTurn(true);
        this.player2.setTurn(false);
    }

    //initalize game board with all the pierces
    private void initializeGameBoard(BoardTheme theme) {
        // Initialize the game board
        Color[] boardColors = (theme != null) ? theme.getColors() : BoardTheme.CLASSIC.getColors();
        this.gameBoard = new Board(this.gamePane, boardColors);

    }












}
