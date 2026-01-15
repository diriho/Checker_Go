# Settings Functionality Implementation

## Overview
The Settings module handles global application preferences and provides information about the software.

## Implementation Details
1.  **UI Layer (`PopupViews.java`)**:
    - **Architecture**: A `VBox` separated into an "About" section and a "Settings" section using a `Separator` node.
    - **About Section**: Contains a `Text` node with wrapping enabled to display the history and description of CheckerGo.
    - **Settings Section**: Contains `CheckBox` controls for:
        - Enable Sound Effects
        - Enable Animations (Defaulted to `true`)

2.  **Logic**:
    - The visuals are implemented using `PopupViews`. Currently, the toggles are UI elements; binding them to the actual audio/animation engines in `Game.java` or `Piece.java` would follow the Observer pattern or a global Configuration singleton.

## Requirements
- **Current**: JavaFX.
- **Future (for full implementation)**:
    - **Configuration Persistence**: Saving the state of the checkboxes (Sound/Animations) to a local preferences file so they persist across restarts.
    - **Asset Linkage**: Connecting the "Sound" toggle to the actual media player handling sound effects.
