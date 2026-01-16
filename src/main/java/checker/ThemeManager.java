package checker;

import javafx.scene.Scene;

public class ThemeManager {
    private static boolean isDarkMode = false;
    private static final String LIGHT_CSS = "/css/light.css";
    private static final String DARK_CSS = "/css/dark.css";

    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String cssPath = isDarkMode ? DARK_CSS : LIGHT_CSS;
        String css = ThemeManager.class.getResource(cssPath).toExternalForm();
        scene.getStylesheets().add(css);
    }

    public static void toggleTheme(Scene scene) {
        isDarkMode = !isDarkMode;
        applyTheme(scene);
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }
}
