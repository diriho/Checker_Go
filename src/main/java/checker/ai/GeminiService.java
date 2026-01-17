package checker.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import checker.data.UserDataManager;

public class GeminiService {
    
    private final HttpClient client;
    private final UserDataManager userDataManager;

    public GeminiService() {
        this.client = HttpClient.newHttpClient();
        this.userDataManager = UserDataManager.getInstance();
    }

    public GeminiService(HttpClient client, UserDataManager userDataManager) {
        this.client = client;
        this.userDataManager = userDataManager;
    }

    /**
     * Asynchronously fetches advice from Gemini based on user stats and their question.
     */
    public CompletableFuture<String> askForAdvice(String userQuestion) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Gather Context
                UserDataManager dm = this.userDataManager;
                String name = dm.getDisplayName();
                int wins = dm.getWins();
                int losses = dm.getLosses();
                int streak = dm.getStreak();
                
                // Construct Prompt
                String contextPrompt = String.format(
                    "You are a friendly and wise experiment Checker's Game Coach. " +
                    "The player's name is %s. " +
                    "They have %d Wins and %d Losses, with a %d-day playing streak. " +
                    "Based on these stats, infer their skill level. " +
                    "If they have few games, they are a beginner. " +
                    "If they have high wins, they are advanced. " +
                    "Address them by name if provided. " +
                    "The user asks: \"%s\". " +
                    "Provide short, strategic advice (max 2 sentences) to help them win.",
                    name, wins, losses, streak, userQuestion
                );

                // Build JSON Request Body for Gemini
                /* Structure:
                   {
                     "contents": [{
                       "parts": [{"text": "prompt..."}]
                     }]
                   }
                */
                JSONObject part = new JSONObject();
                part.put("text", contextPrompt);
                
                JSONArray parts = new JSONArray();
                parts.put(part);
                
                JSONObject contentObj = new JSONObject();
                contentObj.put("parts", parts);
                
                JSONArray contents = new JSONArray();
                contents.put(contentObj);
                
                JSONObject requestBody = new JSONObject();
                requestBody.put("contents", contents);

                // Create Request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(GeminiConfig.API_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                        .build();

                // Send Request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return parseGeminiResponse(response.body());
                } else {
                    return "Error from AI Coach: " + response.statusCode() + " - " + response.body();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Coach is currently unavailable (Connection Error).";
            }
        });
    }

    private String parseGeminiResponse(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            // Navigate: candidates[0] -> content -> parts[0] -> text
            if (json.has("candidates")) {
                JSONArray candidates = json.getJSONArray("candidates");
                if (candidates.length() > 0) {
                    JSONObject candidate = candidates.getJSONObject(0);
                    JSONObject content = candidate.getJSONObject("content");
                    JSONArray parts = content.getJSONArray("parts");
                    return parts.getJSONObject(0).getString("text");
                }
            }
            return "Coach is thinking... (No response generated)";
        } catch (Exception e) {
            return "Could not understand the Coach's advice.";
        }
    }
}
