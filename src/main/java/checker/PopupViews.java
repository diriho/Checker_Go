package checker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PopupViews {
    private PaneOrganizer organizer;

    public PopupViews(PaneOrganizer organizer) {
        this.organizer = organizer;
    }

    private StackPane createBaseWindow(String title, Node content) {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        
        // Window Background
        VBox window = new VBox(15);
        window.setMaxSize(500, 400); // Standard size
        window.setPadding(new Insets(20));
        window.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0);");
        
        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_RIGHT);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Invisible spacer to center title if needed, but simple is close button on right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = new Button("✕");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999; -fx-font-size: 18px; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> organizer.closePopup());
        
        header.getChildren().addAll(titleLabel, spacer, closeBtn);
        
        // Content Wrapper
        if (content instanceof Region) {
             ((Region) content).setMaxWidth(Double.MAX_VALUE);
             VBox.setVgrow(content, Priority.ALWAYS);
        }
        
        window.getChildren().addAll(header, new Separator(), content);
        
        root.getChildren().add(window);
        return root;
    }

    public Node getSettingsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(10));
        
        // About Section
        VBox aboutBox = new VBox(5);
        Label aboutHeader = new Label("About CheckerGo");
        aboutHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Text aboutText = new Text("CheckerGo is a modern take on the classic game of Checkers.\n" +
                                "Enjoy playing against friends or challenging our adaptive AI.\n" + 
                                "Features include multiple themes, difficulty levels, and strict rule enforcement.");
        aboutText.setWrappingWidth(440);
        aboutBox.getChildren().addAll(aboutHeader, aboutText);
        
        // Settings Section
        VBox settingsBox = new VBox(10);
        Label settingsHeader = new Label("Game Settings");
        settingsHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        CheckBox soundToggle = new CheckBox("Enable Sound Effects");
        CheckBox animationsToggle = new CheckBox("Enable Animations");
        animationsToggle.setSelected(true);
        
        settingsBox.getChildren().addAll(settingsHeader, soundToggle, animationsToggle);
        
        content.getChildren().addAll(aboutBox, new Separator(), settingsBox);
        return createBaseWindow("Settings", content);
    }

    public Node getAccountView() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        
        checker.data.UserDataManager dm = checker.data.UserDataManager.getInstance();
        
        // Status Label
        Label status = new Label("Not Signed In");
        
        // --- 1. Auth Form (Email/Pass + Google) ---
        VBox authBox = new VBox(10);
        authBox.setAlignment(Pos.CENTER);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        javafx.scene.control.PasswordField passField = new javafx.scene.control.PasswordField();
        passField.setPromptText("Password");
        
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        
        Button signInBtn = new Button("Sign In");
        signInBtn.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle("-fx-background-color: #4285F4; -fx-text-fill: white; -fx-font-weight: bold;");

        // Google Sign In Button
        Button googleBtn = new Button("Sign in with Google");
        googleBtn.setStyle("-fx-background-color: #DB4437; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        
        buttons.getChildren().addAll(signInBtn, signUpBtn);
        authBox.getChildren().addAll(new Label("Firebase Authentication"), emailField, passField, buttons, googleBtn, errorLabel);
        
        // --- 2. Name Input Form ---
        VBox nameBox = new VBox(10);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setVisible(false);
        nameBox.setManaged(false);
        
        Label askNameLabel = new Label("What would you love to be called?");
        askNameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your display name");
        nameField.setMaxWidth(200);
        
        Button saveNameBtn = new Button("Continue");
        saveNameBtn.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
        
        nameBox.getChildren().addAll(askNameLabel, nameField, saveNameBtn);
        
        // --- 3. Stats View ---
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");
        Label header = new Label("Statistics");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label winsLbl = new Label();
        Label lossLbl = new Label();
        Label streakLbl = new Label();
        
        statsBox.getChildren().addAll(header, winsLbl, lossLbl, streakLbl);
        
        // Sign Out Button (Only visible when logged in)
        Button signOutBtn = new Button("Sign Out");
        
        // --- UI Logic Helper ---
        Runnable refreshState = () -> {
            boolean isAuth = dm.isAuthenticated();
            boolean isGuest = "guest".equals(dm.getCurrentUserId());
            String displayName = dm.getDisplayName();
            boolean hasName = !"Player".equals(displayName) && !displayName.isEmpty();
            
            // Status Text
            if (isAuth) {
                status.setText("Signed in as: " + dm.getCurrentUserId());
            } else {
                status.setText("Playing as Guest");
            }

            // Visibility Logic
            if (!isAuth) {
                // GUEST MODE
                authBox.setVisible(true);
                authBox.setManaged(true);
                nameBox.setVisible(false);
                nameBox.setManaged(false);
                
                // Show Guest Stats? Yes.
                statsBox.setVisible(true);
                statsBox.setManaged(true);
                header.setText("Statistics (Guest)");
                signOutBtn.setVisible(false);
            } else {
                // AUTHENTICATED
                authBox.setVisible(false);
                authBox.setManaged(false);
                
                if (!hasName) {
                    // ASK FOR NAME
                    nameBox.setVisible(true);
                    nameBox.setManaged(true);
                    statsBox.setVisible(false);
                    statsBox.setManaged(false);
                    signOutBtn.setVisible(true); // Allow sign out from name screen
                } else {
                    // SHOW WELCOME & STATS
                    nameBox.setVisible(false);
                    nameBox.setManaged(false);
                    statsBox.setVisible(true);
                    statsBox.setManaged(true);
                    header.setText("Welcome " + displayName);
                    signOutBtn.setVisible(true);
                }
            }
            
            // Update Labels
            winsLbl.setText("Wins: " + dm.getWins());
            lossLbl.setText("Losses: " + dm.getLosses());
            streakLbl.setText("Streak: " + dm.getStreak() + " Days");
        };
        
        // Initial Refresh
        refreshState.run();

        // --- Handlers ---
        checker.auth.FirebaseAuthService authService = new checker.auth.FirebaseAuthService();
        
        signInBtn.setOnAction(e -> {
            org.json.JSONObject result = authService.signIn(emailField.getText(), passField.getText());
            if (result.has("error")) {
                errorLabel.setText(result.getJSONObject("error").getString("message"));
            } else {
                dm.loadUser(result.getString("localId"));
                errorLabel.setText("");
                refreshState.run();
            }
        });
        
        signUpBtn.setOnAction(e -> {
            org.json.JSONObject result = authService.signUp(emailField.getText(), passField.getText());
            if (result.has("error")) {
                errorLabel.setText(result.getJSONObject("error").getString("message"));
            } else {
                dm.loadUser(result.getString("localId"));
                errorLabel.setText("");
                refreshState.run();
            }
        });
        
        googleBtn.setOnAction(e -> {
            // Simulated Google Sign In
            // In a real app, this would open a browser for OAuth.
            // Here we simulate a successful login with a unique ID format.
            String mockGoogleId = "google_user_" + System.currentTimeMillis();
            dm.loadUser(mockGoogleId);
            refreshState.run();
        });
        
        saveNameBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                dm.setDisplayName(name);
                refreshState.run();
            }
        });

        signOutBtn.setOnAction(e -> {
            dm.loadUser("guest");
            emailField.clear(); 
            passField.clear();
            refreshState.run();
        });
        
        content.getChildren().addAll(status, authBox, nameBox, new Separator(), statsBox, signOutBtn);
        return createBaseWindow("Account", content);
    }

    public Node getConnectView() {
        VBox content = new VBox(15);
        
        HBox connectBox = new HBox(10);
        connectBox.setAlignment(Pos.CENTER_LEFT);
        TextField codeField = new TextField();
        codeField.setPromptText("Enter Invite Code");
        Button connectBtn = new Button("Connect");
        connectBtn.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
        connectBox.getChildren().addAll(new Label("Join Game:"), codeField, connectBtn);
        
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPromptText("Chat will appear here once connected...");
        chatArea.setPrefHeight(200);
        
        HBox inputBox = new HBox(10);
        TextField msgInput = new TextField();
        HBox.setHgrow(msgInput, Priority.ALWAYS);
        Button sendBtn = new Button("Send");
        inputBox.getChildren().addAll(msgInput, sendBtn);
        inputBox.setDisable(true); // Disable until connected
        
        connectBtn.setOnAction(e -> {
            chatArea.appendText("Connected to Room: " + codeField.getText() + "\n");
            inputBox.setDisable(false);
        });
        
        sendBtn.setOnAction(e -> {
            if (!msgInput.getText().isEmpty()) {
                chatArea.appendText("Me: " + msgInput.getText() + "\n");
                msgInput.clear();
            }
        });
        
        content.getChildren().addAll(connectBox, new Separator(), new Label("Chat"), chatArea, inputBox);
        return createBaseWindow("Connect", content);
    }

    public Node getAIChatView() {
        VBox content = new VBox(15);
        
        CheckBox allowData = new CheckBox("Allow AI to use my game data for analysis");
        allowData.setSelected(true);
        
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setText("Coach: Hello! I've analyzed your stats. Ask me for strategy advice!\n");
        chatArea.setPrefHeight(200);
        chatArea.setWrapText(true);
        
        HBox inputBox = new HBox(10);
        TextField msgInput = new TextField();
        HBox.setHgrow(msgInput, Priority.ALWAYS);
        Button sendBtn = new Button("Ask");
        inputBox.getChildren().addAll(msgInput, sendBtn);
        
        checker.ai.GeminiService aiService = new checker.ai.GeminiService();

        sendBtn.setOnAction(e -> {
            String q = msgInput.getText().trim();
            if (!q.isEmpty()) {
                chatArea.appendText("You: " + q + "\n");
                msgInput.clear();
                
                if (allowData.isSelected()) {
                     chatArea.appendText("Coach: Thinking...\n");
                     
                     // Call Gemini Async
                     aiService.askForAdvice(q).thenAccept(advice -> {
                         javafx.application.Platform.runLater(() -> {
                             chatArea.appendText("Coach: " + advice + "\n\n");
                         });
                     });
                } else {
                     chatArea.appendText("Coach: Please enable data analysis permissions so I can help.\n");
                }
            }
        });
        
        content.getChildren().addAll(allowData, chatArea, inputBox);
        return createBaseWindow("AI Strategy Chat", content);
    }
}
