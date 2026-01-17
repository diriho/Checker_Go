package checker;

// constant class
public class constants {
    
    // Firebase Configuration
    public static final String FIREBASE_API_KEY = "AIzaSyAKWdGiyLbWbDpn-PLuhAfUNtF_5xYwwnc";
    public static final String FIREBASE_AUTH_DOMAIN = "checker-go.firebaseapp.com";
    public static final String FIREBASE_PROJECT_ID = "checker-go";
    public static final String FIREBASE_STORAGE_BUCKET = "checker-go.firebasestorage.app";
    public static final String FIREBASE_MESSAGING_SENDER_ID = "841160803651";
    public static final String FIREBASE_APP_ID = "1:841160803651:web:987cb7ad807d8c2a5a5bf6";
    public static final String FIREBASE_MEASUREMENT_ID = "G-D1SY46M09D";

    public static final String FIREBASE_SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;
    public static final String FIREBASE_SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;
    public static final String FIREBASE_USER_DATA_URL = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=" + FIREBASE_API_KEY;

    // Gemini Configuration
    public static final String GEMINI_API_KEY = "AIzaSyDcqENq5L_iY_7lvaw2ovdATbH2ccIjhGc"; 
    public static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + GEMINI_API_KEY;

    // Board Configuration
    public static final int SQUARE_SIZE = 50;
    public static final int BOARD_SIZE = 10;
    public static final int BOARD_OFFSET = 20;

    // UI Layout Configuration
    public static final int GAME_PANE_WIDTH = 550;
    public static final int GAME_PANE_HEIGHT = 550;
    public static final int SIDE_PANE_WIDTH = 200;
    public static final int BOTTOM_PANE_HEIGHT = 100;
    
    public static final int WINDOW_WIDTH = GAME_PANE_WIDTH + SIDE_PANE_WIDTH;
    public static final int WINDOW_HEIGHT = GAME_PANE_HEIGHT + BOTTOM_PANE_HEIGHT;

    public static final int POPUP_WIDTH = 500;
    public static final int POPUP_HEIGHT = 400;

    // Color Configuration
    public static final String COLOR_SIGN_IN = "#2E8B57";
    public static final String COLOR_SIGN_UP = "#4285F4";
    public static final String COLOR_GOOGLE = "#DB4437";
    public static final String COLOR_FIND_MATCH = "#4682B4";
    public static final String COLOR_CREATE_ROOM = "#8A2BE2";
    public static final String COLOR_START_GAME = "#32CD32";
    public static final String COLOR_VS_COMPUTER = "#FFD700";
    public static final String COLOR_VS_HUMAN = "#FFA07A";
    public static final String COLOR_CHAT_SEND = "#0078FF";

}
