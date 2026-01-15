# Connect Functionality Implementation

## Overview
The Connect feature facilitates multiplayer interaction by allowing users to join game rooms via a shared code and communicate via real-time chat.

## Implementation Details
1.  **UI Layer (`PopupViews.java`)**:
    - **Connection Interface**: An `HBox` containing a `TextField` for the "Invite Code" and a "Connect" button.
    - **Chat Interface**: A read-only `TextArea` for history and a `TextField` for input.
    - **State Management**: The chat input is disabled (`setDisable(true)`) until the user successfully "connects" (simulated by clicking the Connect button).

2.  **Logic**:
    - When "Connect" is clicked, the application simulates joining a room by enabling the chat input and printing a confirmation message to the chat area.
    - Messages sent by the user are appended to the chat area with the prefix "Me: ".

## Requirements
- **Current**: JavaFX.
- **Future (for full implementation)**:
    - **Networking**: A backend server (WebSocket or TCP Socket) to relay messages and game moves between clients.
    - **Encryption**: Logic to generate and validate the "Invite Code" to ensure secure private rooms.
    - **Synchronization**: Mechanisms to keep the game board in sync between two remote players (sending move coordinates).
