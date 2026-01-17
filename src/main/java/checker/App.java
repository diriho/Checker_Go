package checker;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        PaneOrganizer paneOrganizer = new PaneOrganizer();  
        Scene scene = new Scene(paneOrganizer.getRoot(), constants.WINDOW_WIDTH, constants.WINDOW_HEIGHT);
        
        // Apply initial theme
        ThemeManager.applyTheme(scene);
        
        stage.setTitle("Checker Go!");  
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
