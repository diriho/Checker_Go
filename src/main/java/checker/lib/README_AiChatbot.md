# AI Chatbot Functionality Implementation

## Overview
The AI Chatbot has been significantly upgraded from a mock responder to a **real-time strategic coach** powered by **Google's Gemini Flash API**. It now provides personalized advice based on the player's actual game statistics.

## New Features
1.  **Gemini AI Integration**: 
    - Replaced hardcoded responses with dynamic calls to the `gemini-flash-latest` model (faster and more optimized for short tasks than the pro model).
    - Added `checker.ai.GeminiService` to handle asynchronous HTTP requests.
    - Added `checker.constants` to store API configuration (Keys/URLs).

2.  **Context-Aware Advice**:
    - The AI receives a system prompt containing the user's **Name**, **Wins**, **Losses**, and **Streak**.
    - **Personalization**: If the user is signed in, the AI addresses them by their "Display Name".
    - **Skill Extrapolation**: The AI infers the player's skill level (Beginner vs. Veteran) based on their win/loss ratio and tailors the complexity of the advice accordingly.

3.  **UI Updates**:
    - The chat window now displays a "Thinking..." state while waiting for the async API response to prevent freezing the UI (JavaFX Application Thread safety implemented using `Platform.runLater`).
    - The "Allow Use of Data" toggle now functionally controls access to the API. If unchecked, the local client blocks the request.

## Technical Details

### Architecture
- **Service Class**: `checker.ai.GeminiService`
    - Uses `java.net.http.HttpClient` for non-blocking requests.
    - Uses `CompletableFuture` to handle the API response asynchronously.
    - Parses complex JSON responses from Google's Generative Language API using `org.json`.

### Data Privacy
- **Consent**: The user *must* check "Allow AI to use my game data" for the system to work.
- **Data Shared**: Only statistical aggregates (Wins, Losses, Streak, Name) and the specific text question are sent to the LLM. No raw game board data is transmitted in this version.

## Usage
1.  **Configuration**: Users must add their Gemini API Key to `checker.constants`.
2.  **Interaction**:
    - Open "AI Strategy Chat".
    - Type a question (e.g., "How do I beat a defensive player?").
    - The Coach responds: *"Hey [Name]! Since you're on a [X] game streak, try aggressive center control..."*

## Requirements
- **API Key**: Valid Google AI Studio API Key required in `checker.constants`.
- **Internet**: Active connection required to reach `generativelanguage.googleapis.com`.

## Updates & Changes (Jan 15, 2026)
- **Model Upgrade**: Switched from `gemini-pro` to `gemini-flash-latest` to resolve API availability issues and improve response latency.
- **Endpoint Update**: Configured to use the `v1beta` API endpoint for compatibility with the latest models.
- **Dependencies**: Verified `org.json` is correctly parsing the nested candidate/content/part structure of the Gemini response.
