package checker;

import javafx.scene.paint.Color;

public interface MoveStrategy {
    Move chooseMove(Board board, Color activeColor);
}
