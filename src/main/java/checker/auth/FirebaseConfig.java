package checker.auth;

public class FirebaseConfig {
    // Configuration constants provided by the user
    public static final String API_KEY = "AIzaSyAKWdGiyLbWbDpn-PLuhAfUNtF_5xYwwnc";
    public static final String AUTH_DOMAIN = "checker-go.firebaseapp.com";
    public static final String PROJECT_ID = "checker-go";
    public static final String STORAGE_BUCKET = "checker-go.firebasestorage.app";
    public static final String MESSAGING_SENDER_ID = "841160803651";
    public static final String APP_ID = "1:841160803651:web:987cb7ad807d8c2a5a5bf6";
    public static final String MEASUREMENT_ID = "G-D1SY46M09D";

    // REST API Endpoints for Identity Toolkit
    public static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
    public static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
    public static final String USER_DATA_URL = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=" + API_KEY;
}
