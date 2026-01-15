package checker;

// Move class to represent a move in the game
public class Move {
    Pierce piece;
    int fromRow, fromCol;
    int toRow, toCol;
    boolean isCapture;
    int jumpRow, jumpCol;

    // Constructor for Move class
    public Move(Pierce p, int fr, int fc, int tr, int tc, boolean capture, int jr, int jc) {
        this.piece = p;
        this.fromRow = fr;
        this.fromCol = fc;
        this.toRow = tr;
        this.toCol = tc;
        this.isCapture = capture;
        this.jumpRow = jr;
        this.jumpCol = jc;
    }
}