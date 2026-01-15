package checker;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class bottomBar {
    private HBox pane;
    private PaneOrganizer organizer;

    public bottomBar(Pane parentPane, PaneOrganizer organizer) {
        this.organizer = organizer;
        if (parentPane instanceof HBox) {
            this.pane = (HBox) parentPane;
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

        // Action Handlers
        accountBtn.setOnAction(e -> this.organizer.showPopup(new PopupViews(organizer).getAccountView()));
        connectBtn.setOnAction(e -> this.organizer.showPopup(new PopupViews(organizer).getConnectView()));
        aiChatBtn.setOnAction(e -> this.organizer.showPopup(new PopupViews(organizer).getAIChatView()));
        settingsBtn.setOnAction(e -> this.organizer.showPopup(new PopupViews(organizer).getSettingsView()));
        
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
