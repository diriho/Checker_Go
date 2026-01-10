# Checker_Go
A Java-based Checkers game built with JavaFX featuring three AI difficulty levels (easy, intermediate, hard) and a secure authentication system that enables online multiplayer and performance tracking. The project also integrates an AI-powered strategy chatbot to help players learn tactics, analyze gameplay, and improve their chances of winning.

## AI Difficulty Levels Implementation
The computer opponent (`ComputerPlayer`) has been refactored to use a Strategy Pattern, allowing for pluggable AI implementations implementing the `MoveStrategy` interface.

### 1. Easy Mode (`EasyAI`)
- **Strategy:** Random Selection.
- **Implementation:** The AI identifies all legal moves on the board using a lightweight board simulation (`VirtualBoard`). From this list, it selects one move completely at random.
- **Complexity:** Depth 0 (No lookahead).

### 2. Medium Mode (`MediumAI`)
- **Strategy:** Minimax Algorithm.
- **Implementation:** Uses a Minimax search algorithm with a depth of 4 plies. It simulates future board states to minimize potential losses and maximize gains.
- **Evaluation:** Scores board states based on piece count and King count (Kings are weighted 3x higher than regular pieces).
- **Expectation:** The computer plays defensively and tactically, looking several moves ahead to avoid traps.

### 3. Hard Mode (`HardAI`)
- **Strategy:** Minimax with Alpha-Beta Pruning.
- **Implementation:** Uses an optimized Minimax algorithm with Alpha-Beta pruning to search deeper (depth 6) without performance penalties.
- **Evaluation:** employs a sophisticated evaluation function considering:
    - **Material:** Piece and King counts.
    - **Center Control:** Bonus for controlling the center columns (3-6).
    - **Promotion Potential:** Bonus for advancing pieces toward the King row.
- **Expectation:** The computer plays highly aggressively and strategically, competing for board control and utilizing advanced heuristics to outsmart the opponent.

## How to?
This program uses Maven as an automation and dependency management tool.
To compile the program run ``mvn compile``
Then run ``mvn javafx:run``

Shortcut: ``mvn clean compile javafx:run`` 

