package checker;

import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends HumanPlayer {

    private MoveStrategy strategy;

    // Constructor for ComputerPlayer class
    public ComputerPlayer(Board board, Color color) {
        this(board, color, Difficulty.EASY);
    }

    public ComputerPlayer(Board board, Color color, Difficulty difficulty) {
        super(board, color);
        setDifficulty(difficulty);
    }
    
    public void setDifficulty(Difficulty diff) {
        switch (diff) {
            case HARD:
                this.strategy = new HardAI();
                break;
            case MEDIUM:
                this.strategy = new MediumAI();
                break;
            case EASY:
            default:
                this.strategy = new EasyAI();
                break;
        }
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
        Move selectedMove = this.strategy.chooseMove(this.board, this.myColor);
        
        if (selectedMove == null) {
            // No moves available
             if (this.onTurnEnd != null) {
                this.onTurnEnd.run();
            }
            return;
        }
        
        executeMove(selectedMove);
    }

    private void executeMove(Move move) {
        this.selectedPiece = move.piece;
        
        if (move.isCapture) {
             executeCaptureMove(move.fromRow, move.fromCol, move.toRow, move.toCol, move.jumpRow, move.jumpCol);
             
             if (this.selectedPiece != null) {
                 PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                 pause.setOnFinished(e -> continueMultiJump(this.selectedPiece));
                 pause.play();
             }
        } else {
             executeSimpleMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }
    }

    private void continueMultiJump(Pierce p) {
        // Use VirtualBoard to find only capture moves for this specific piece
        VirtualBoard vb = new VirtualBoard(this.board);
        int r = -1, c = -1;
        // Find p coords
        for(int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                if(this.board.getMyPierces()[i][j] == p) { r=i; c=j; break; }
            }
        }
        
        if (r == -1) { finalizeTurn(); return; }
        
        byte pType = vb.grid[r][c];
        List<VirtualBoard.VMove> moves = new ArrayList<>();
        List<VirtualBoard.VMove> captures = new ArrayList<>();
        
        // We need to access the private helper 'addMovesForPiece' in VB or reimplement.
        // Since VB methods are private, we can just use getLegalMoves and filter.
        List<VirtualBoard.VMove> allMoves = vb.getLegalMoves(pType);
        
        for (VirtualBoard.VMove vm : allMoves) {
            if (vm.fromR == r && vm.fromC == c && vm.isCapture) {
                captures.add(vm);
            }
        }
        
        if (!captures.isEmpty()) {
             // Just pick one (Handling branching in multi-jump is rare/complex)
             VirtualBoard.VMove vNext = captures.get(0);
             Move nextMove = convertToRealMove(vNext);
             executeMove(nextMove);
        } else {
             finalizeTurn();
        }
    }
    
    private Move convertToRealMove(VirtualBoard.VMove vm) {
        Pierce p = this.board.getMyPierces()[vm.fromR][vm.fromC];
        int jumpR = -1, jumpC = -1;
        if (vm.isCapture && !vm.capturedPos.isEmpty()) {
            jumpR = vm.capturedPos.get(0)[0];
            jumpC = vm.capturedPos.get(0)[1];
        }
        return new Move(p, vm.fromR, vm.fromC, vm.toR, vm.toC, vm.isCapture, jumpR, jumpC);
    }
}

