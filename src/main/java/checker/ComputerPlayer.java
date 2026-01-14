package checker;

import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
        // Use strategy to pick the best continuation for this specific piece
        // The strategy will respect the 'forcedPiece' constraint and pick the best path
        Move nextMove = this.strategy.chooseMove(this.board, this.myColor, p);
        
        if (nextMove != null) {
             executeMove(nextMove);
        } else {
             finalizeTurn();
        }
    }
    
    // Legacy helper kept if needed, but strategy handles conversion now usually.
    // Actually strategy returns Move, so we don't need convertToRealMove here anymore 
    // UNLESS strategy is calling it. MoveStrategy impls call their own convert.
    // So we can remove convertToRealMove from ComputerPlayer or keep it if used elsewhere.
    // It is not used elsewhere in this file based on my reading.
    // But I will keep it to minimize diff churn just in case, or remove it to be clean.
    // Strategy returns 'checker.Move', executeMove takes 'checker.Move'.
    
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

