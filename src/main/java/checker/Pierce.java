package checker;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class Pierce implements PieceInterface {
    private StackPane view;
    private Circle bgCircle;
    private Label crown;
    private Color color;
    private int x;
    private int y;
    private boolean king;

    // Pierce class constructor
    public Pierce(Color color, int x, int y) {  
        this.color = color;
        this.x = x;
        this.y = y;
        this.king = false;
        this.setupPierce(this.color, this.x, this.y);

    }   

    // setup and initialize pierce properties
    private void setupPierce(Color color, int x, int y ) {
        this.view = new StackPane();
        
        this.bgCircle = new Circle(20, color);
        this.bgCircle.setStroke(Color.BLACK);
        this.bgCircle.setStrokeWidth(2);
        
        this.crown = new Label("♔");
        this.crown.setFont(new Font(24));
        // Contrast color for crown
        if(color.equals(Color.BLACK)) {
            this.crown.setTextFill(Color.WHITE);
        } else {
            this.crown.setTextFill(Color.BLACK);
        }
        this.crown.setVisible(false);
        
        this.view.getChildren().addAll(this.bgCircle, this.crown);
        
        // Position the StackPane
        // x,y are Center coordinates of the square/circle
        // StackPane is top-left based by default layout but we can center it
        this.view.setLayoutX(x - 20);
        this.view.setLayoutY(y - 20);
        
        // Ensure StackPane doesn't grow
        this.view.setMaxSize(40, 40);
        this.view.setMinSize(40, 40);
    }

    // getter method of getting the pierce object
    public Node getNode() {
        return this.view;
    } 
    
    // Legacy support if needed, but prefer getNode
    public Circle getCircle() {
        return this.bgCircle;
    }

    // getter method of getting the pierce coordinates
    public int[] getPierceCoords() {
        return new  int[] {this.x, this.y};
    }

    // getter method of getting the pierce color
    public Color getColor() {
        return this.color;
    }

    // setter method of changing the pierce color
    public void setColor(Color color) {
        this.color = color;    
        this.bgCircle.setFill(color);
    }

    // setter method of changing the pierce coordinates
    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
        this.view.setLayoutX(x - 20);
        this.view.setLayoutY(y - 20);
    }
    
    @Override
    public boolean isKing() {
        return this.king;
    }
    
    @Override
    public void makeKing() {
        this.king = true;
        this.crown.setVisible(true);
    }

}
