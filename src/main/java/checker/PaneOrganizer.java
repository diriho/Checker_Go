package checker;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @author dondestiniriho
 */


public class PaneOrganizer {
    private StackPane root; // Changed to StackPane to support overlays
    private BorderPane mainLayout; // The original root
    private VBox sidePane;
    private Pane gamePane;  
    private HBox bottomPane;
    private StackPane popupContainer;
    
    // Constructor of the PaneOrganizer
    public PaneOrganizer() {
        // Initialize and organize your panes here
         this.root = new StackPane();
         this.mainLayout = new BorderPane();
         
         this.root.getChildren().add(this.mainLayout);
         
         // Setup popup container (hidden by default)
         this.popupContainer = new StackPane();
         this.popupContainer.setAlignment(Pos.CENTER);
         this.popupContainer.setVisible(false);
         // Consume clicks effectively blocking the back
         this.popupContainer.setOnMouseClicked(e -> {
             // Optional: Close on background click? 
             // userRequest says "A new window has to pop up... click on any of those buttons". 
             // Usually popups serve a purpose. Let's add a close button inside the popups instead.
         });
         
         this.root.getChildren().add(this.popupContainer);

         this.setSidePane();
         this.setBottomPane();
         this.setGamePane();
    }
    
    public void showPopup(Node content) {
        this.popupContainer.getChildren().clear();
        this.popupContainer.getChildren().add(content);
        this.popupContainer.setVisible(true);
        this.mainLayout.setEffect(new GaussianBlur(10));
    }
    
    public void closePopup() {
        this.popupContainer.setVisible(false);
        this.popupContainer.getChildren().clear();
        this.mainLayout.setEffect(null);
    }


    // setup the main game pane
    private void setGamePane() {
        this.gamePane = new Pane();
        this.gamePane.setPrefSize(550, 550);
        this.gamePane.setStyle("-fx-background-color: beige");
        this.gamePane.setFocusTraversable(true);
        this.mainLayout.setCenter(this.gamePane); 
        new Game(this.gamePane, this.sidePane);

    }

    // set the side pane which will handle controls and information display
    private void setSidePane() {
        this.sidePane = new VBox();
        this.sidePane.setPrefWidth(200);
        this.sidePane.setStyle("-fx-background-color: #dcd4bbff");
        this.mainLayout.setRight(this.sidePane); 
    }   

    // set the bottom pane for restart, quit, and menu options
    private void setBottomPane() {
        this.bottomPane = new HBox();
        this.bottomPane.setPrefHeight(100);
        this.bottomPane.setStyle("-fx-background-color: #D2B48C");
        this.mainLayout.setBottom(this.bottomPane);
        new bottomBar(this.bottomPane, this); // Pass this
    }   

    // Getter method for the root pane
    public StackPane getRoot() {
        return this.root;
    }
    
    public Pane getGamePane() { return this.gamePane; }
    public Pane getSidePane() { return this.sidePane; }
}
  