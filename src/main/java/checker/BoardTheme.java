package checker;

import javafx.scene.paint.Color;

public enum BoardTheme {
    CLASSIC("Classic", Color.BEIGE, Color.BROWN),
    FOREST("Forest Green", Color.rgb(236, 237, 212), Color.rgb(129, 151, 98)),
    WARM("Warm Mahogany", Color.rgb(234, 209, 167), Color.rgb(137, 91, 59));

    private final String name;
    private final Color lightColor;
    private final Color darkColor;

    BoardTheme(String name, Color lightColor, Color darkColor) {
        this.name = name;
        this.lightColor = lightColor;
        this.darkColor = darkColor;
    }

    public String getName() {
        return name;
    }

    public Color[] getColors() {
        return new Color[]{lightColor, darkColor};
    }
    
    @Override
    public String toString() {
        return name;
    }
}
