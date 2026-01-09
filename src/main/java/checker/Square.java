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
        this.square = new Rectangle(this.x , this.y, 30, 30);
        square.setFill(this.color);
        square.setStroke(Color.BLACK);
        square.setStrokeWidth(1);

    }

    // getter methods
    public int[] getCoords() {
        return new int[] {this.x, this.y};
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Rectangle getSquare() {
        return this.square;
    }
}
