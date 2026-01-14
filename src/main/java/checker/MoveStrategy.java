package checker;

import javafx.scene.paint.Color;

public interface MoveStrategy {
    Move chooseMove(Board board, Color activeColor);
    
    // Default method for forced moves (multi-jump continuation)
    default Move chooseMove(Board board, Color activeColor, Pierce forcedPiece) {
        return chooseMove(board, activeColor);
    }
}
