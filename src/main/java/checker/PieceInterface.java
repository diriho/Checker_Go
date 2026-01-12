package checker;

import javafx.scene.Node;
import javafx.scene.paint.Color;    


public interface PieceInterface {
    // method signatures for piece properties and behaviors
    public Node getNode();
    public Color getColor();
    public void setColor(Color color);
    public int[] getPierceCoords();
    public void setCoords(int x, int y);
    public boolean isKing();
    public void makeKing();
}
