import java.util.prefs.Preferences;

public class HighscoreManager {
    private static final String HIGH_SCORE_KEY = "highScore";
    private Preferences prefs;

    public HighscoreManager() {
        prefs = Preferences.userNodeForPackage(getClass());
    }

    public void newHighscore(int score) {
        prefs.putInt(HIGH_SCORE_KEY, score);
    }

    public int readHighscore() {
        return prefs.getInt(HIGH_SCORE_KEY, 0);
    }

    public void resetHighscore() {
        prefs.putInt(HIGH_SCORE_KEY, 0);
    }
}
