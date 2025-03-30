import java.util.prefs.Preferences;

public class HighscoreManager {
    private static final String EASY_HIGH_SCORE_KEY = "easyHighScore";
    private static final String MEDIUM_HIGH_SCORE_KEY = "highScore";
    private static final String HARD_HIGH_SCORE_KEY = "hardHighScore";
    private Preferences prefs;

    public HighscoreManager() {
        prefs = Preferences.userNodeForPackage(getClass());
    }

    // Easy
    public void newEasyHighscore(int score) {
        prefs.putInt(EASY_HIGH_SCORE_KEY, score);
    }

    public int readEasyHighscore() {
        return prefs.getInt(EASY_HIGH_SCORE_KEY, 0);
    }

    public void resetEasyHighscore() {
        prefs.putInt(EASY_HIGH_SCORE_KEY, 0);
    }

    // Medium
    public void newMediumHighscore(int score) {
        prefs.putInt(MEDIUM_HIGH_SCORE_KEY, score);
    }

    public int readMediumHighscore() {
        return prefs.getInt(MEDIUM_HIGH_SCORE_KEY, 0);
    }

    public void resetMediumHighscore() {
        prefs.putInt(MEDIUM_HIGH_SCORE_KEY, 0);
    }

    // Hard
    public void newHardHighscore(int score) {
        prefs.putInt(HARD_HIGH_SCORE_KEY, score);
    }

    public int readHardHighscore() {
        return prefs.getInt(HARD_HIGH_SCORE_KEY, 0);
    }

    public void resetHardHighscore() {
        prefs.putInt(HARD_HIGH_SCORE_KEY, 0);
    }

    public void resetAllHighscores() {
        prefs.putInt(EASY_HIGH_SCORE_KEY, 0);
        prefs.putInt(MEDIUM_HIGH_SCORE_KEY, 0);
        prefs.putInt(HARD_HIGH_SCORE_KEY, 0);
    }
}
