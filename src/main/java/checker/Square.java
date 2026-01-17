package checker;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Square {
    // Square class instance variables 
    private int x;
    private int y;
    private Color color;
    private Rectangle square;

    // Square class constructor
    public Square(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.setupSquare();

    }

    // Setup square properties 
    private void setupSquare() {
        this.square = new Rectangle(this.x , this.y, constants.SQUARE_SIZE, constants.SQUARE_SIZE);
        square.setFill(this.color);
        square.setStroke(Color.BLACK);
        square.setStrokeWidth(1);

    }

    // getter methods
    public int[] getCoords() {
        return new int[] {this.x, this.y};
    }

    // get color of the square
    public Color getColor() {
        return this.color;
    }

    // set the color of the square
    public void setColor(Color color) {
        this.color = color;
    }

    // get the square Node which will be displayed to the pane a children
    public Rectangle getSquare() {
        return this.square;
    }
}
