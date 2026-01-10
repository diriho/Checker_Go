package checker;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * @author dondestiniriho
 */


public class PaneOrganizer {
    private BorderPane root;
    private VBox sidePane;
    private Pane gamePane;  
    private HBox bottomPane;
    
    // Constructor of the PaneOrganizer
    public PaneOrganizer() {
        // Initialize and organize your panes here
         this.root = new BorderPane();
         this.setSidePane();
         this.setBottomPane();
         this.setGamePane();
    }


    // setup the main game pane
    private void setGamePane() {
        this.gamePane = new Pane();
        this.gamePane.setPrefSize(550, 550);
        this.gamePane.setStyle("-fx-background-color: beige");
        this.gamePane.setFocusTraversable(true);
        this.root.setCenter(this.gamePane); 
        new Game(this.gamePane, this.sidePane);

    }

    // set the side pane which will handle controls and information display
    private void setSidePane() {
        this.sidePane = new VBox();
        this.sidePane.setPrefWidth(200);
        this.sidePane.setStyle("-fx-background-color: #dcd4bbff");
        this.root.setRight(this.sidePane); 
    }   

    // set the bottom pane for restart, quit, and menu options
    private void setBottomPane() {
        this.bottomPane = new HBox();
        this.bottomPane.setPrefHeight(100);
        this.bottomPane.setStyle("-fx-background-color: #D2B48C");
        this.root.setBottom(this.bottomPane);
    }   

    // Getter method for the root pane
    public BorderPane getRoot() {
        return this.root;
    }
}
 