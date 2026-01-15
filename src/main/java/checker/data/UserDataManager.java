package checker.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Properties;

public class UserDataManager {
    // Singleton for Auth State if needed, but instance is fine if managed by UI
    // We will now support switching users.
    
    private Properties stats;
    private File file;
    private static UserDataManager instance;
    private String currentUserId = "guest"; // Default to guest
    private boolean isAuthenticated = false;

    // Singleton Pattern for easy access across app
    public static UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    public UserDataManager() {
        // Ensure directory exists
        File dir = new File("src/main/java/checker/data/stats");
        if (!dir.exists()) dir.mkdirs();
        
        loadUser("guest");
    }
    
    public void loadUser(String userId) {
        this.currentUserId = userId;
        String fileName = "user_stats_" + userId + ".properties";
        File dir = new File("src/main/java/checker/data/stats");
        this.file = new File(dir, fileName);
        this.stats = new Properties();
        this.isAuthenticated = !"guest".equals(userId);
        
        loadStats();
    }
    
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    
    // Load stats from disk
    private void loadStats() {
        if (!file.exists()) {
            initDefaults();
            return;
        }
        try (InputStream input = new FileInputStream(file)) {
            stats.load(input);
        } catch (IOException ex) {
            initDefaults();
        }
    }
    
    private void initDefaults() {
        stats.setProperty("wins", "0");
        stats.setProperty("losses", "0");
        stats.setProperty("streak", "0");
        stats.setProperty("last_played", "1970-01-01");
        stats.setProperty("display_name", "Player");
        saveStats();
    }
    
    // Save to disk
    private void saveStats() {
        try (OutputStream output = new FileOutputStream(file)) {
            stats.store(output, "CheckerGo User Statistics");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    // Getters
    public int getWins() { return Integer.parseInt(stats.getProperty("wins", "0")); }
    public int getLosses() { return Integer.parseInt(stats.getProperty("losses", "0")); }
    public int getStreak() { return Integer.parseInt(stats.getProperty("streak", "0")); }
    public String getDisplayName() { return stats.getProperty("display_name", "Player"); }
    public String getProfilePicturePath() { return stats.getProperty("profile_pic", null); }

    public void setDisplayName(String name) {
        stats.setProperty("display_name", name);
        saveStats();
    }

    public void setProfilePicturePath(String path) {
        if (path != null) {
            stats.setProperty("profile_pic", path);
        } else {
            stats.remove("profile_pic");
        }
        saveStats();
    }

    // Logic
    public void recordGame(boolean won) {
        if (won) {
            int w = getWins();
            stats.setProperty("wins", String.valueOf(w + 1));
            updateStreak();
        } else {
            int l = getLosses();
            stats.setProperty("losses", String.valueOf(l + 1));
            // Streak reset? Prompt implies "day streak". 
            // Usually valid usage is losing doesn't reset *daily* login streak, 
            // but might reset *winning* streak. 
            // User asked for "day streak" -> implies usage consistency, not win streak.
            updateStreak(); 
        }
        saveStats();
    }
    
    private void updateStreak() {
        String lastDateStr = stats.getProperty("last_played", "1970-01-01");
        LocalDate lastDate = LocalDate.parse(lastDateStr);
        LocalDate today = LocalDate.now();
        
        if (lastDate.equals(today)) {
            // Already played today, streak remains
        } else if (lastDate.plusDays(1).equals(today)) {
            // Consecutive day
            int s = getStreak();
            stats.setProperty("streak", String.valueOf(s + 1));
        } else {
            // Gap > 1 day, reset streak to 1
            stats.setProperty("streak", "1");
        }
        
        stats.setProperty("last_played", today.toString());
    }
}
