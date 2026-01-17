package checker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import checker.ai.GeminiConfig;
import checker.ai.GeminiService;
import checker.data.UserDataManager;

@ExtendWith(MockitoExtension.class)
public class GeminiApiTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    @Mock
    private UserDataManager mockUserDataManager;

    private GeminiService geminiService;

    @BeforeEach
    public void setUp() {
        geminiService = new GeminiService(mockHttpClient, mockUserDataManager);
    }

    @Test
    public void testAskForAdviceSuccess() throws IOException, InterruptedException, ExecutionException {
        // Arrange
        String userQuestion = "What is the best move?";
        String adviceText = "Move your king to the center.";
        
        // Mock User Data
        when(mockUserDataManager.getDisplayName()).thenReturn("TestUser");
        when(mockUserDataManager.getWins()).thenReturn(10);
        when(mockUserDataManager.getLosses()).thenReturn(5);
        when(mockUserDataManager.getStreak()).thenReturn(3);

        // Mock API Response Structure
        JSONObject textPart = new JSONObject().put("text", adviceText);
        JSONArray partsArr = new JSONArray().put(textPart);
        JSONObject contentObj = new JSONObject().put("parts", partsArr);
        JSONArray candidatesArr = new JSONArray().put(new JSONObject().put("content", contentObj));
        JSONObject successJson = new JSONObject().put("candidates", candidatesArr);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(successJson.toString());
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Act
        CompletableFuture<String> futureResult = geminiService.askForAdvice(userQuestion);
        String result = futureResult.get(); // Blocks until complete

        // Assert
        assertEquals(adviceText, result);

        // Verify Dependencies
        verify(mockUserDataManager).getDisplayName();
        verify(mockUserDataManager).getWins();
        
        // Verify Request
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        
        HttpRequest sentRequest = requestCaptor.getValue();
        assertEquals(URI.create(GeminiConfig.API_URL), sentRequest.uri());
        assertEquals("POST", sentRequest.method());
    }

    @Test
    public void testAskForAdviceApiFailure() throws IOException, InterruptedException, ExecutionException {
        // Arrange
        // Mock User Data (needed to build prompt before request)
        when(mockUserDataManager.getDisplayName()).thenReturn("TestUser");
        
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Act
        CompletableFuture<String> futureResult = geminiService.askForAdvice("Help me");
        String result = futureResult.get();

        // Assert
        assertTrue(result.contains("Error from AI Coach"));
        assertTrue(result.contains("500"));
        assertTrue(result.contains("Internal Server Error"));
        
        // Dependencies checked by verifying simple successful mocking interaction
        verify(mockHttpClient).send(any(), any());
    }

    @Test
    public void testAskForAdviceException() throws IOException, InterruptedException, ExecutionException {
        // Arrange
        when(mockUserDataManager.getDisplayName()).thenReturn("TestUser");
        
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network Unreachable"));

        // Act
        CompletableFuture<String> futureResult = geminiService.askForAdvice("Help me");
        String result = futureResult.get();

        // Assert
        assertTrue(result.contains("Coach is currently unavailable"));
        assertTrue(result.contains("Connection Error"));
    }
}
