# Checker_Go
A Java-based Checkers game built with JavaFX featuring three AI difficulty levels (easy, intermediate, hard) and a secure authentication system that enables online multiplayer and performance tracking. The project also integrates an AI-powered strategy chatbot to help players learn tactics, analyze gameplay, and improve their chances of winning.

## AI Difficulty Levels Implementation
The computer opponent (`ComputerPlayer`) features three distinct difficulty levels, each employing a different strategy for selecting moves. This ensures players of all skill levels can find an appropriate challenge. All levels prioritize capturing moves if available, adhering to standard checkers rules where captures are often mandatory or highly advantageous.

### 1. Easy Mode
- **Strategy:** Random Selection.
- **Implementation:** The AI identifies all valid moves available on the board. From this list, it selects one move completely at random using `java.util.Random`.
- **Expectation:** The computer plays unpredictably and makes no attempt to defend pieces or control the board. It is prone to making mistakes, making it suitable for beginners learning the basic mechanics.

### 2. Medium Mode
- **Strategy:** Edge Safety.
- **Implementation:** The AI prioritizes moves that land pieces on the "safe" edges of the board (columns 0 and 9). In checkers, pieces on the edge are generally harder to capture because they cannot be jumped from the outside. If no edge moves are available, it falls back to a random move.
- **Expectation:** The computer plays slightly more defensively. By hugging the walls, it reduces the number of exposed pieces, offering a moderate challenge without using complex forward planning.

### 3. Hard Mode
- **Strategy:** Center Control.
- **Implementation:** The AI prioritizes moves that advance pieces into the center columns of the board (columns 3-6). Ruling the center allows pieces more mobility and options to attack in multiple directions, a core tenet of advanced checkers strategy. If no center-bound moves are available, it falls back to random selection.
- **Expectation:** The computer plays aggressively to dominate the board's center. This maximizes its offensive potential and mobility, providing a tougher challenge for experienced players who must fight for board control.

## How to?
This program uses Maven as an automation and dependency management tool.
To compile the program run ``mvn compile``
Then run ``mvn javafx:run``

Shortcut: ``mvn clean compile javafx:run`` 

