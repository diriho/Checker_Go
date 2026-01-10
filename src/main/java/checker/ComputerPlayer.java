package checker;

import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends HumanPlayer {

    private Difficulty difficulty;
    private Random random;

    // Constructor for ComputerPlayer class
    public ComputerPlayer(Board board, Color color) {
        this(board, color, Difficulty.EASY);
    }

    public ComputerPlayer(Board board, Color color, Difficulty difficulty) {
        super(board, color);
        this.difficulty = difficulty;
        this.random = new Random();
    }

    @Override
    protected void setUpMouseHandler() {
        // Do nothing. Computer does not use mouse input.
    }

    @Override
    public void setTurn(boolean isTurn) {
        super.setTurn(isTurn);
        if (isTurn) {
            // Simulate thinking time
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> makeMove());
            pause.play();
        }
    }

    private void makeMove() {
        List<Move> validMoves = getAllValidMoves();
        
        if (validMoves.isEmpty()) {
            // handle inability to move (pass turn)
             if (this.onTurnEnd != null) {
                this.onTurnEnd.run();
            }
            return;
        }

        Move selectedMove = null;

        switch (this.difficulty) {
            case EASY:
                selectedMove = getEasyMove(validMoves);
                break;
            case MEDIUM:
                selectedMove = getMediumMove(validMoves);
                break;
            case HARD:
                selectedMove = getHardMove(validMoves);
                break;
        }
        
        if (selectedMove != null) {
            executeMove(selectedMove);
        }
    }

    private Move getEasyMove(List<Move> moves) {
        // Random move
        return moves.get(this.random.nextInt(moves.size()));
    }

    private Move getMediumMove(List<Move> moves) {
        // Prefer moves that go to the edge (safer)
        List<Move> edgeMoves = new ArrayList<>();
        for (Move m : moves) {
            if (m.toCol == 0 || m.toCol == 9) {
                edgeMoves.add(m);
            }
        }
        
        if (!edgeMoves.isEmpty()) {
            return edgeMoves.get(this.random.nextInt(edgeMoves.size()));
        }
        
        return getEasyMove(moves);
    }

    private Move getHardMove(List<Move> moves) {
        // Prioritize captures first (if any capture moves exist, List<Move> passes captures first
        // based on how we built it in getAllValidMoves, BUT captures are prioritized only if we return ONLY captures
        // Currently getAllValidMoves returns EITHER captures OR simple moves.
        // So we just need to pick the "best" move from the list.
        
        // Hard Strategy: Center Control
        List<Move> centerMoves = new ArrayList<>();
         for (Move m : moves) {
            if (m.toCol >= 3 && m.toCol <= 6) {
                centerMoves.add(m);
            }
        }
        
        if (!centerMoves.isEmpty()) {
            return centerMoves.get(this.random.nextInt(centerMoves.size()));
        }

        return getEasyMove(moves);
    }

    private void executeMove(Move move) {
        this.selectedPiece = move.piece;
        
        if (move.isCapture) {
             executeCaptureMove(move.fromRow, move.fromCol, move.toRow, move.toCol, move.jumpRow, move.jumpCol);
        } else {
             executeSimpleMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }
    }

    private List<Move> getAllValidMoves() {
        Pierce[][] grid = this.board.getMyPierces();
        
        // 1. Gather all captures first (forced captures usually required, but allowed here)
        List<Move> captures = new ArrayList<>();
        // 2. Gather simple moves
        List<Move> simpleMoves = new ArrayList<>();
        
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                Pierce p = grid[r][c];
                if (p != null && this.myPierces.contains(p)) {
                    addMovesForPiece(p, r, c, captures, simpleMoves);
                }
            }
        }
        
        // Prioritize captures if available? (Common checkers rule, but logic is flexible)
        // Merging them for now, but putting captures first so Hard AI might pick them
        if (!captures.isEmpty()) {
            return captures; 
        }
        return simpleMoves;
    }

    private void addMovesForPiece(Pierce p, int r, int c, List<Move> captures, List<Move> simpleMoves) {
        int direction = (this.myColor.equals(Color.WHITE)) ? -1 : 1;
        
        // Simple Moves (Forward only)
        int nextRow = r + direction;
        if (nextRow >= 0 && nextRow < 10) {
            // Left
            if (c - 1 >= 0 && this.board.getMyPierces()[nextRow][c - 1] == null) {
                simpleMoves.add(new Move(p, r, c, nextRow, c - 1, false, -1, -1));
            }
            // Right
            if (c + 1 < 10 && this.board.getMyPierces()[nextRow][c + 1] == null) {
                simpleMoves.add(new Move(p, r, c, nextRow, c + 1, false, -1, -1));
            }
        }
        
        // Capture Moves (All 4 diagonals)
        int[] dr = {-2, -2, 2, 2};
        int[] dc = {-2, 2, -2, 2};
        
        for (int i = 0; i < 4; i++) {
             int nr = r + dr[i];
             int nc = c + dc[i];
             
             if (nr >= 0 && nr < 10 && nc >= 0 && nc < 10) {
                 if (this.board.getMyPierces()[nr][nc] == null) {
                     int mr = (r + nr) / 2;
                     int mc = (c + nc) / 2;
                     Pierce mid = this.board.getMyPierces()[mr][mc];
                     
                     if (mid != null && !this.myPierces.contains(mid)) {
                         captures.add(new Move(p, r, c, nr, nc, true, mr, mc));
                     }
                 }
             }
        }
    }
}
