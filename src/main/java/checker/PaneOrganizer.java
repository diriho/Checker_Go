package checker;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
/**
 *
 * @author dondestiniriho
 */
public class PaneOrganizer {
    private BorderPane root;

    public PaneOrganizer() {
        // Initialize and organize your panes here
         this.root = new BorderPane();
         this.setGamePane();
         this.setSidePane();
         this.setBottomPane();
    }


    // setup the main game pane
    public void setGamePane() {
        Pane gamePane = new Pane();
        gamePane.setPrefSize(920, 920);
        gamePane.setStyle("-fx-background-color: lightgray; -fx-border-color: gray;");
        gamePane.setFocusTraversable(true);
        this.root.setCenter(gamePane); 

    }

    // set the side pane which will handle controls and information display
    public void setSidePane() {
        VBox sidePane = new VBox();
        sidePane.setPrefWidth(200);
        sidePane.setStyle("-fx-background-color:  #524e37ff; -fx-border-color: bluesky;");
        this.root.setRight(sidePane); 
    }   

    // set the bottom pane for restart, quit, and menu options
    public void setBottomPane() {
        HBox bottomPane = new HBox();
        bottomPane.setPrefHeight(100);
        bottomPane.setStyle("-fx-background-color: #010922ff");
        this.root.setBottom(bottomPane); 
    }   

    // Getter method for the root pane
    public BorderPane getRoot() {
        return this.root;
    }
}
 