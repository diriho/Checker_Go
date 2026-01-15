# Account Functionality Implementation

## Overview
The Account feature has been overhauled to provide a complete user identity experience, including authentication, personalization, and data persistence.

## 1. Authentication & Identity
We support three modes of access:
1.  **Guest Mode**: Default for new users. Stats are saved locally but not tied to an account.
2.  **Email/Password Auth**: Uses Firebase REST API to create secure accounts.
3.  **Google Sign-In**: A simulated OAuth flow that provides a "Sign in with Google" experience (assigns a unique `google_user_ID` for data tracking).

### Personalization ("What would you love to be called?")
To make the experience welcoming, we added a **Name Personalization Flow**:
- Upon first login (Email/Google), if a user has not set a Display Name, they are prompted: *"What would you love to be called?"*
- This name is stored persistently in the user's profile properties.
- The UI greets the user with **"Welcome [Name]"** instead of a generic header.

## 2. Technical Implementation

### Auth System (Firebase REST)
- **`checker.auth.FirebaseAuthService`**: Direct HTTP calls to Google Identity Toolkit.
- **Dependencies**: Uses `org.json` for parsing JSON responses.

### Data Persistence (`UserDataManager.java`)
- **Singleton Pattern**: The manager is a global singleton, allowing the Game UI and Popup UI to share the same user state.
- **Dynamic File Switching**:
    - Guest: `user_stats_guest.properties`
    - Auth User: `user_stats_{UID}.properties`
- **Storage Location**: All stats files are now organized in a dedicated folder:  
  `src/main/java/checker/data/stats/` (added to `.gitignore`).
- **Stored Properties**:
  - `wins`, `losses`, `streak`, `last_played`
  - `display_name` (New)

## 3. UI Implementation (`PopupViews.java`)
The Account View is now a dynamic state machine with three views:
1.  **Auth View**: Login forms and "Sign in with Google" button.
2.  **Onboarding View**: "What would you love to be called?" input field.
3.  **Stats View**: Displays "Welcome [Name]", Wins, Losses, Streaks, and "Sign Out".

## 4. Updates & Changes (Jan 15, 2026)
- **Moved Stats**: Stats files relocated to `checker/data/stats` to keep the project clean.
- **Personalization**: Added the Name Prompt and `display_name` persistence.
- **Improved UX**: Users are no longer "User 12345" but "Welcome [Name]".

## Usage Flow
1.  Open "Account".
2.  Click "Sign in with Google" (or use Email/Pass).
3.  Enter your desired Display Name when prompted.
4.  See your personalized stats dashboard.
5.  Play games; stats are recorded to your specific file.
