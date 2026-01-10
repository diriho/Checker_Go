package checker;

import javafx.scene.paint.Color;

public class kingPiece extends Pierce implements PieceInterface {

    // kingPiece class constructor 
    public kingPiece(Color color, int x, int y){
        super(color, x, y); // since it extends Pierce class as its a super class
        //this.isKing = true;
    }
    
}
