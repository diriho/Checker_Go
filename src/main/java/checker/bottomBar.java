package checker;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class bottomBar {
    private HBox pane;

    public bottomBar(Pane parentPane) {
        if (parentPane instanceof HBox) {
            this.pane = (HBox) parentPane;
        } else {
             // Fallback if not passed an HBox, though PaneOrganizer uses HBox
             // We can just add children to parentPane if generic
             // But for alignment, let's assume it IS the pane to populate
        }
        
        setupButtons();
    }

    private void setupButtons() {
        this.pane.setAlignment(Pos.CENTER);
        this.pane.setSpacing(40); // Space between buttons

        Button accountBtn = createStyledButton("Account", "👤");
        Button connectBtn = createStyledButton("Connect", "🔗");
        Button aiChatBtn = createStyledButton("AI Chatbot", "🤖");
        Button settingsBtn = createStyledButton("Settings", "⚙️");

        this.pane.getChildren().addAll(accountBtn, connectBtn, aiChatBtn, settingsBtn);
    }

    private Button createStyledButton(String text, String icon) {
        Button btn = new Button(icon + " " + text);
        
        // Style: 
        // Background: Darker shade of the bar (#D2B48C is Tan, let's use a Brown or Sienna)
        // Text: White or Dark Brown
        String normalStyle = 
            "-fx-background-color: #8B4513;" + // SaddleBrown
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-min-width: 120px;" +
            "-fx-min-height: 40px;";
            
        String hoverStyle = 
            "-fx-background-color: #A0522D;" + // Sienna (slightly lighter)
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-min-width: 120px;" +
            "-fx-min-height: 40px;";

        btn.setStyle(normalStyle);

        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        
        return btn;
    }
}
