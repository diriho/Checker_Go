package checker;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Pierce implements PieceInterface {
    private Circle pierce;
    private Color color;
    private int x;
    private int y;
    private Board board;

    // Pierce class constructor
    public Pierce(Color color, Board board, int x, int y) {
       this.board = board;   
        this.color = color;
        this.x = x;
        this.y = y;
        this.setupPierce(this.color, board, this.x, this.y);

    }   

    // setup and initialize pierce properties
    private void setupPierce(Color color, Board board, int x, int y ) {
        this.pierce = new Circle(20, color);
        pierce.setCenterX(x);
        pierce.setCenterY(y);
        pierce.setStroke(Color.BLACK);
        pierce.setStrokeWidth(2);

    }

    // getter methods
    // getter method of getting the pierce object
    public Circle getPierce() {
        return this.pierce;
    }   

    // getter method of getting the pierce coordinates
    public int[] getPierceCoords() {
        return new  int[] {this.x, this.y};
    }

    // getter method of getting the pierce color
    public Color getColor() {
        return this.color;
    }

    // getter method of getting the pierce circle
    public Circle getCircle() {
        return this.pierce;
    }   

    // setter method of changing the pierce color
    public void setColor(Color color) {
        this.color = color;    
    }

    // setter method of changing the pierce coordinates
    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
