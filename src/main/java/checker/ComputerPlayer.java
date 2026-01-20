package checker;

import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ComputerPlayer extends HumanPlayer {

    private MoveStrategy strategy;
    private PauseTransition thinkingPause;
    private PauseTransition jumpPause;

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
            this.thinkingPause = new PauseTransition(Duration.seconds(1));
            this.thinkingPause.setOnFinished(e -> makeMove());
            this.thinkingPause.play();
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        if (this.thinkingPause != null) this.thinkingPause.stop();
        if (this.jumpPause != null) this.jumpPause.stop();
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
                 this.jumpPause = new PauseTransition(Duration.seconds(0.5));
                 this.jumpPause.setOnFinished(e -> continueMultiJump(this.selectedPiece));
                 this.jumpPause.play();
             }
        } else {
             executeSimpleMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
        }
    }

    @Override
    protected void executeCaptureMove(int oldRow, int oldCol, int targetRow, int targetCol, int jumpedRow, int jumpedCol) {
        // --- Board Update Logic (Copied from HumanPlayer to avoid super.executeCaptureMove side effects) ---
        
        // Remove captured piece
        Pierce capturedPiece = this.board.getMyPierces()[jumpedRow][jumpedCol];
        this.board.getMyPierces()[jumpedRow][jumpedCol] = null;
        if (capturedPiece != null) {
            this.board.getPane().getChildren().remove(capturedPiece.getNode());
        }
        
        this.updateBoardAndPiece(oldRow, oldCol, targetRow, targetCol);
        
        // CRITICAL CHANGE: We do NOT call finalizeTurn() or set forcedPiece here.
        // The ComputerPlayer's async logic (continueMultiJump) controls the turn flow.
        // Calling super.executeCaptureMove() would trigger finalizeTurn() if no further jumps are valid,
        // causing a race condition with our PauseTransition in executeMove().
    }

    private void continueMultiJump(Pierce p) {
        // Use strategy to pick the best continuation for this specific piece
        // The strategy will respect the 'forcedPiece' constraint and pick the best path
        Move nextMove = this.strategy.chooseMove(this.board, this.myColor, p);
        
        // FIX: Ensure that the continuation move is strictly a CAPTURE.
        // It is illegal to make a simple move in the middle of a multi-jump sequence.
        // VirtualBoard might return simple moves if no captures exist, so we must filter them out.
        if (nextMove != null && nextMove.isCapture) {
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

