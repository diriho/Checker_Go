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
    public void onTurnComplete() {
        if (checkGameOver()) {
            // Stop game
            this.player1.setTurn(false);
            this.player2.setTurn(false);
            // System.out.println("Game Over");
            // In a real app, show an Alert or restart dialog
        }
    }

    private boolean checkGameOver() {
        // active player to move is determined by logic, but here we check BOTH to see if one has 0 moves
        // If current turn player has 0 moves, they lost.
        // We need to know whose turn it is. 
        // Currently Game doesn't track "turnColor", the Players have a boolean flag.
        
        // Let's check both players.
        boolean p1CanMove = hasLegalMoves(Color.BLACK);
        boolean p2CanMove = hasLegalMoves(Color.WHITE);
        
        if (!p1CanMove) {
            displayWin(Color.WHITE);
            return true;
        }
        if (!p2CanMove) {
            displayWin(Color.BLACK);
            return true;
        }
        return false;
    }
    
    private boolean hasLegalMoves(Color color) {
        // Use VirtualBoard to check for valid moves
        VirtualBoard vb = new VirtualBoard(this.gameBoard);
        byte cByte = (color == Color.WHITE) ? VirtualBoard.WHITE_MAN : VirtualBoard.BLACK_MAN;
        return !vb.getLegalMoves(cByte).isEmpty();
    }
    
    private void displayWin(Color winner) {
        javafx.scene.control.Label winMsg = new javafx.scene.control.Label(winner == Color.WHITE ? "White Wins!" : "Black Wins!");
        winMsg.setStyle("-fx-font-size: 40px; -fx-text-fill: red; -fx-background-color: rgba(255,255,255,0.8); -fx-padding: 20px;");
        // Center it
        winMsg.setTranslateX(150);
        winMsg.setTranslateY(200);
        this.gamePane.getChildren().add(winMsg);
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
            this.onTurnComplete(); // Check before switching? No, usually after turn, we check if NEXT player can move
            // Actually standard is: Current player played. Next player is scanned.
            // My checkGameOver scans both. So it handles "Next player stuck" immediately.
            
            // Check if game already ended
            if (!hasLegalMoves(Color.WHITE) && !hasLegalMoves(Color.BLACK)) return; // Double block? Rare.
            
            this.player1.setTurn(false);
            this.player2.setTurn(true);
            this.onTurnComplete(); // Check if newly active player has moves (if not, they lose immediately)
        });

        this.player2.setOnTurnEnd(() -> {
            this.player2.setTurn(false);
            this.player1.setTurn(true);
            this.onTurnComplete();
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
