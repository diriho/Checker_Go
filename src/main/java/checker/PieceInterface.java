package checker;

import javafx.scene.paint.Color;    
import javafx.scene.shape.Circle;   


public interface PieceInterface {
    // method signatures for piece properties and behaviors
    public Circle getCircle();
    public Color getColor();
    public void setColor(Color color);
    public int[] getPierceCoords();
    public void setCoords(int x, int y);




}
