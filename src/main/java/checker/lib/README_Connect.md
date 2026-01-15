# Connect Functionality Implementation

## Overview
The Connect feature now facilitates online matchmaking logic, allowing users to find opponents in real-time or join private rooms. It handles user searching, match notifications, and fallback proposals when no opponents are available.

## Implementation Details
1.  **UI Layer (`PopupViews.java`)**:
    - **Main Menu**: Options to "Find Online Match", "Create Private Room", or "Join Private Room".
    - **Search View**: Displays a progress indicator while simulating a network search for active players (`searching...`).
    - **Lobby View**: Displays a generated **Room Code**, waits for an opponent connection (simulated), and provides a "Start Game" button once ready.
    - **Match Found View**: Displays the opponent's name, profile picture (fetched dynamically), and rank, with options to "Accept" or "Decline".
    - **Fallback View**: If no match is found, specific notifications guide the user to alternative options.

2.  **Private Room & Code Generation**:
    - **Creation**: Users can create a private room which generates a unique 6-character alphanumeric code.
    - **Algorithm**: The code is generated using a `Random` selection from the set `[A-Z, 0-9]`.
        - *Example*: `A7X92B`
        - *Process*: A `StringBuilder` appends 6 random characters from the allowed pool. This provides roughly 2 billion (36^6) unique combinations, sufficient for a casual game lobby to avoid collisions.
    - **Gameplay**: Once the opponent joins (simulated) and the host clicks "Start Game", the application initializes a **Human vs Human** game session (`resetGame(false, ...)`) on the local board, ensuring that both connected players can make moves on the same game instance without disturbing the core logic.

3.  **Matchmaking Logic**:
    - **Search Simulation**: The system runs an asynchronous search (using `PauseTransition`) to simulate scanning for connected users.
    - **Opponent Discovery**: The algorithm tries to find an available "in-time" connected user. 
    - **Mock Data**: For demonstration, it randomly successfully matches ~60% of the time, generating opponent names and fetching avatars (via `ui-avatars.com`).

3.  **Fallback Mechanism**:
    - When the algorithm determines no users are available, it displays a notification: "No Online Players Found".
    - **Proposals**: The system intelligently proposes valid alternatives as initially implemented:
        - **Play vs Computer**: Redirects the user to the local AI game mode.
        - **Play vs Local Friend**: Redirects the user to the local hot-seat multiplayer mode.

## Requirements
- **Current**: JavaFX, Internet Connection (for Avatar fetching).
- **Future (for full implementation)**:
    - **Backend Server**: WebSocket server to maintain the list of active connected users (`LobbyService`).
    - **Matchmaking Algorithm**: Server-side logic to pair users based on ELO/Rank instead of random chance.
    - **P2P Connection**: Establishing the actual game session once "Accept" is clicked.
    - **Synchronization**: Mechanisms to keep the game board in sync between two remote players.
