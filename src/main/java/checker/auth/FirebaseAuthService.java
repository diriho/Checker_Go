package checker.auth;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import checker.constants;

public class FirebaseAuthService {
    private final HttpClient httpClient;

    public FirebaseAuthService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Signs up a new user with email and password.
     * @return JSONObject containing idToken, localId, email, etc. or error details.
     */
    public JSONObject signUp(String email, String password) {
        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("returnSecureToken", true);

        return sendRequest(constants.FIREBASE_SIGN_UP_URL, payload);
    }

    /**
     * Signs in an existing user with email and password.
     * @return JSONObject containing idToken, localId, email, etc. or error details.
     */
    public JSONObject signIn(String email, String password) {
        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", password);
        payload.put("returnSecureToken", true);

        return sendRequest(constants.FIREBASE_SIGN_IN_URL, payload);
    }

    private JSONObject sendRequest(String url, JSONObject payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Check for non-200 status codes which indicate errors from Firebase
            if (response.statusCode() != 200) {
                 JSONObject errorParams = new JSONObject(response.body());
                 // Wrap error in a structure handleable by UI
                 if (errorParams.has("error")) {
                     return errorParams; // Return the error object directly
                 }
                 // If weird format, wrap it
                 JSONObject err = new JSONObject();
                 JSONObject details = new JSONObject();
                 details.put("message", "HTTP Error " + response.statusCode() + ": " + response.body());
                 err.put("error", details);
                 return err;
            }

            return new JSONObject(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject err = new JSONObject();
            JSONObject details = new JSONObject();
            details.put("message", "Connection Exception: " + e.getMessage());
            err.put("error", details);
            return err;
        }
    }
}
