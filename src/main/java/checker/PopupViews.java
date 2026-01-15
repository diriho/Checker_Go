package checker;

import java.io.File;
import java.util.Random;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
        statsBox.setAlignment(Pos.CENTER);

        // Profile Picture
        javafx.scene.image.ImageView profileView = new javafx.scene.image.ImageView();
        profileView.setFitWidth(80);
        profileView.setFitHeight(80);
        profileView.setPreserveRatio(true);
        // Circular clip
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(40, 40, 40);
        profileView.setClip(clip);
        
        Button changePicBtn = new Button("Change Picture");
        changePicBtn.setStyle("-fx-font-size: 10px;");
        
        Label header = new Label("Statistics");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label winsLbl = new Label();
        Label lossLbl = new Label();
        Label streakLbl = new Label();
        
        statsBox.getChildren().addAll(header, profileView, changePicBtn, winsLbl, lossLbl, streakLbl);
        
        // Sign Out Button (Only visible when logged in)
        Button signOutBtn = new Button("Sign Out");
        
        // --- UI Logic Helper ---
        Runnable refreshState = () -> {
            boolean isAuth = dm.isAuthenticated();
            boolean isGuest = "guest".equals(dm.getCurrentUserId());
            String displayName = dm.getDisplayName();
            boolean hasName = !"Player".equals(displayName) && !displayName.isEmpty();
            String picPath = dm.getProfilePicturePath();

            // Load Image
            if (picPath != null && !picPath.isEmpty()) {
                try {
                    File imgFile = new File(picPath);
                    if (imgFile.exists()) {
                         profileView.setImage(new javafx.scene.image.Image(imgFile.toURI().toString()));
                    } else {
                         // Default avatar if file missing
                         // Ideally load a resource, but here we can just set null or a web icon
                         profileView.setImage(null);
                    }
                } catch (Exception e) {
                    profileView.setImage(null);
                }
            } else {
                profileView.setImage(null);
            }
            // Hide change button if guest or no image logic desired for guest
            // Let's allow guests to have local pics too if they want
            
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
                changePicBtn.setVisible(true); // Allow guests to set pic locally
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
                    changePicBtn.setVisible(true);
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
        
        changePicBtn.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Select Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                // Store path
                dm.setProfilePicturePath(selectedFile.getAbsolutePath());
                refreshState.run();
            }
        });
        
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
        StackPane rootContent = new StackPane();
        rootContent.setPadding(new Insets(10));
        
        // --- View 1: Main Menu ---
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        
        Label title = new Label("Online Multiplayer");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Button findMatchBtn = new Button("Find Online Match");
        findMatchBtn.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 250; -fx-padding: 10;");
        
        Button createRoomBtn = new Button("Create Private Room");
        createRoomBtn.setStyle("-fx-background-color: #8A2BE2; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 250; -fx-padding: 10;");
        
        HBox joinBox = new HBox(10);
        joinBox.setAlignment(Pos.CENTER);
        TextField codeField = new TextField();
        codeField.setPromptText("Enter Room Code");
        Button joinBtn = new Button("Join Private Room");
        joinBox.getChildren().addAll(codeField, joinBtn);
        
        menuBox.getChildren().addAll(title, findMatchBtn, createRoomBtn, new Separator(), new Label("Or join a friend:"), joinBox);
        
        // --- View: Create Room (Lobby) ---
        VBox lobbyBox = new VBox(20);
        lobbyBox.setAlignment(Pos.CENTER);
        lobbyBox.setVisible(false); // Explicitly set invisible initially
        lobbyBox.setManaged(false);
        
        Label lobbyTitle = new Label("Private Room Created");
        lobbyTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        HBox codeDisplayBox = new HBox(10);
        codeDisplayBox.setAlignment(Pos.CENTER);
        
        Label codeLabel = new Label("------");
        codeLabel.setStyle("-fx-font-size: 32px; -fx-font-family: 'Courier New'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-border-color: #333; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #ffffff; -fx-background-radius: 5;");
        
        Button copyCodeBtn = new Button("Copy");
        copyCodeBtn.setOnAction(e -> {
            ClipboardContent cc = new ClipboardContent();
            cc.putString(codeLabel.getText());
            Clipboard.getSystemClipboard().setContent(cc);
            copyCodeBtn.setText("Copied!");
            // Reset text after 2 seconds
            PauseTransition pt = new PauseTransition(Duration.seconds(2));
            pt.setOnFinished(evt -> copyCodeBtn.setText("Copy"));
            pt.play();
        });
        
        codeDisplayBox.getChildren().addAll(codeLabel, copyCodeBtn);
        
        Label waitingLabel = new Label("Waiting for opponent to join...");
        ProgressIndicator lobbyProgress = new ProgressIndicator();
        
        Button lobbyStartBtn = new Button("Start Game");
        lobbyStartBtn.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        lobbyStartBtn.setDisable(true);
        
        Button lobbyCancelBtn = new Button("Cancel Room");
        
        lobbyBox.getChildren().addAll(lobbyTitle, new Label("Share this code:"), codeDisplayBox, lobbyProgress, waitingLabel, lobbyStartBtn, lobbyCancelBtn);
        
        // --- View 2: Searching ---
        VBox searchBox = new VBox(20);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setVisible(false);
        searchBox.setManaged(false); // Hide layout space when not visible
        
        Label searchStatus = new Label("Searching for connected players...");
        searchStatus.setStyle("-fx-font-size: 14px;");
        ProgressIndicator progress = new ProgressIndicator();
        Button cancelSearchBtn = new Button("Cancel Search");
        
        searchBox.getChildren().addAll(progress, searchStatus, cancelSearchBtn);
        
        // --- View 3: Match Found ---
        VBox matchBox = new VBox(15);
        matchBox.setAlignment(Pos.CENTER);
        matchBox.setVisible(false);
        matchBox.setManaged(false);
        
        Label matchTitle = new Label("Opponent Found!");
        matchTitle.setStyle("-fx-font-size: 22px; -fx-text-fill: #2E8B57; -fx-font-weight: bold;");
        
        ImageView oppImage = new ImageView();
        oppImage.setFitWidth(120); 
        oppImage.setFitHeight(120);
        oppImage.setPreserveRatio(true);
        Circle clip = new Circle(60, 60, 60);
        oppImage.setClip(clip);
        
        Label oppNameLbl = new Label();
        oppNameLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label oppRankLbl = new Label("Rank: Beginner");
        
        HBox actionBtns = new HBox(15);
        actionBtns.setAlignment(Pos.CENTER);
        Button startBtn = new Button("Accept & Play");
        startBtn.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        Button declineBtn = new Button("Decline");
        actionBtns.getChildren().addAll(startBtn, declineBtn);
        
        matchBox.getChildren().addAll(matchTitle, oppImage, oppNameLbl, oppRankLbl, actionBtns);
        
        // --- View 4: No Match Found (Fallback) ---
        VBox noMatchBox = new VBox(15);
        noMatchBox.setAlignment(Pos.CENTER);
        noMatchBox.setVisible(false);
        noMatchBox.setManaged(false);
        
        Label noMatchTitle = new Label("No Online Players Found");
        noMatchTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #B22222; -fx-font-weight: bold;");
        
        Label suggestion = new Label("We couldn't connect you with anyone at the moment.\nWhy not try one of these options?");
        suggestion.setWrapText(true);
        suggestion.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Button vsComBtn = new Button("Play vs Computer");
        vsComBtn.setStyle("-fx-base: #FFD700;");
        Button vsHumanBtn = new Button("Play vs Local Friend");
        vsHumanBtn.setStyle("-fx-base: #FFA07A;");
        Button retryBtn = new Button("Try Again");
        
        noMatchBox.getChildren().addAll(noMatchTitle, suggestion, vsComBtn, vsHumanBtn, retryBtn);
        
        // --- Logic & wiring ---
        
        // 1. Search Logic
        findMatchBtn.setOnAction(e -> {
            menuBox.setVisible(false);
            menuBox.setManaged(false);
            searchBox.setVisible(true);
            searchBox.setManaged(true);
            
            // Artificial delay to simulate network search
            PauseTransition pause = new PauseTransition(Duration.seconds(3.5));
            pause.setOnFinished(ev -> {
                searchBox.setVisible(false);
                searchBox.setManaged(false);
                
                // Simulate "Algorithm going through in-time connected users"
                // 60% chance to find a mock user
                if (Math.random() > 0.4) { 
                    // Match Found!
                    matchBox.setVisible(true);
                    matchBox.setManaged(true);
                    
                    // Mock Data Generation
                    String[] names = {"CheckerKing99", "Sarah_Plays", "Guest_User_123", "DeepMind_Alpha"};
                    String name = names[new Random().nextInt(names.length)];
                    oppNameLbl.setText(name);
                    
                    // Try to load a generic avatar from web, fallback to null (handled by UI)
                    try {
                        // Using UI Avatars API for dynamic initials
                        String url = "https://ui-avatars.com/api/?name=" + name + "&background=random&size=128&rounded=true";
                        oppImage.setImage(new Image(url, true)); // load in background
                    } catch (Exception ex) { 
                        // If offline, maybe set a local color block or nothing
                    }
                } else {
                    // No Match Found
                    noMatchBox.setVisible(true);
                    noMatchBox.setManaged(true);
                }
            });
            pause.play();
            
            cancelSearchBtn.setOnAction(ce -> {
                pause.stop();
                searchBox.setVisible(false);
                searchBox.setManaged(false);
                menuBox.setVisible(true);
                menuBox.setManaged(true);
            });
        });
        
        // 2. Match Actions
        startBtn.setOnAction(e -> {
            // In a real app, this would start a network game. 
            // Here we just close the popup and assume the game starts locally.
            organizer.closePopup();
        });
        
        declineBtn.setOnAction(e -> {
            matchBox.setVisible(false);
            matchBox.setManaged(false);
            menuBox.setVisible(true);
            menuBox.setManaged(true);
        });
        
        // 3. Fallback Actions
        vsComBtn.setOnAction(e -> {
             // Proposal accepted: Play vs Computer
             organizer.closePopup();
             // User can now select Difficulty on the SideBar
        });
        
        vsHumanBtn.setOnAction(e -> {
             // Proposal accepted: Play Local
             organizer.closePopup();
        });
        
        retryBtn.setOnAction(e -> {
             noMatchBox.setVisible(false);
             noMatchBox.setManaged(false);
             findMatchBtn.fire(); // Restart search
        });
        
        // 4. Join Room Logic (Simple Stub for now)
        joinBtn.setOnAction(e -> {
            if (!codeField.getText().isEmpty()) {
                // Simulate joining
                 menuBox.setVisible(false);
                 menuBox.setManaged(false);
                 searchBox.setVisible(true);
                 searchBox.setManaged(true);
                 searchStatus.setText("Connecting to Room " + codeField.getText() + "...");
                 
                 PauseTransition codePause = new PauseTransition(Duration.seconds(2));
                 codePause.setOnFinished(ev -> {
                      searchBox.setVisible(false);
                      searchBox.setManaged(false);
                      noMatchBox.setVisible(true); // Fail for demo
                      noMatchBox.setManaged(true);
                      noMatchTitle.setText("Room Connect Failed");
                 });
                 codePause.play();
            }
        });
        
        // 5. Create Room Logic
        createRoomBtn.setOnAction(e -> {
            menuBox.setManaged(false);
            menuBox.setVisible(false);
            lobbyBox.setManaged(true);
            lobbyBox.setVisible(true);
            
            // Generate Code
            String roomCode = generateRoomCode();
            codeLabel.setText(roomCode);
            
            // Artificial "Wait" for opponent
            PauseTransition waitOpponent = new PauseTransition(Duration.seconds(4)); // 4 seconds to simulate friend joining
            waitOpponent.setOnFinished(ev -> {
                waitingLabel.setText("Player2 Joined!");
                lobbyProgress.setVisible(false);
                lobbyStartBtn.setDisable(false); // Enable start
            });
            waitOpponent.play();
            
            lobbyCancelBtn.setOnAction(ce -> {
                waitOpponent.stop();
                lobbyBox.setManaged(false);
                lobbyBox.setVisible(false);
                menuBox.setManaged(true);
                menuBox.setVisible(true);
            });
            
            lobbyStartBtn.setOnAction(se -> {
                 organizer.closePopup();
                 // Start networked game (simulated as local PvP)
                 Game game = new Game(organizer.getGamePane(), organizer.getSidePane());
                 // Explicitly set to Human vs Human
                 game.resetGame(false, Difficulty.EASY);
            });
        });
        
        rootContent.getChildren().addAll(menuBox, searchBox, matchBox, noMatchBox, lobbyBox);
        return createBaseWindow("Connect", rootContent);
    }
    
    private String generateRoomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
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
