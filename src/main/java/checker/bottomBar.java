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
        btn.getStyleClass().add("bottom-bar-btn");
        return btn;
    }
}
