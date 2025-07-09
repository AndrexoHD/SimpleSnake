import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;

public class FirebaseLeaderboard {

    private static final String EASY_DATABASE_URL = "https://andrexo-snake-leaderboard-default-rtdb.europe-west1.firebasedatabase.app/easyLeaderboard.json";
    private static final String MEDIUM_DATABASE_URL = "https://andrexo-snake-leaderboard-default-rtdb.europe-west1.firebasedatabase.app/mediumLeaderboard.json";
    private static final String HARD_DATABASE_URL = "https://andrexo-snake-leaderboard-default-rtdb.europe-west1.firebasedatabase.app/hardLeaderboard.json";

    public static int score = 0;

    public static Map<String, Integer> getRemoteLeaderboard(String difficulty) throws Exception {
        URL url = null;
        switch (difficulty) {
            case GamePanel.EASY_DIFFICULTY:
                url = new URL(EASY_DATABASE_URL);
                break;
            case GamePanel.MEDIUM_DIFFICULTY:
                url = new URL(MEDIUM_DATABASE_URL);
                break;
            case GamePanel.HARD_DIFFICULTY:
                url = new URL(HARD_DATABASE_URL);
                break;
            default:
                throw new Exception("A non-valid difficulty was passed to getRemoteLeaderboard()");
        }
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        Map<String, Integer> leaderboard = new HashMap<>();
        if (!response.toString().equals(null)) {
            JSONObject json = new JSONObject(response.toString());
            for (String key : json.keySet()) {
                leaderboard.put(key, json.getInt(key));
            }
        }
        // sortieren und zurückgeben:
        Map<String, Integer> result = new TreeMap<>((a, b) -> {
            Integer va = leaderboard.get(a);
            Integer vb = leaderboard.get(b);
            if (va == null && vb == null) return a.compareTo(b);
            if (va == null) return 1;
            if (vb == null) return -1;
            return vb - va;
        });
        result.putAll(leaderboard);
        return result;
    }

    public static void uploadLeaderboard(Map<String, Integer> lb, String difficulty) throws Exception {
        String URL_TO_CONNECT_TO="";
        switch (difficulty) {
            case GamePanel.EASY_DIFFICULTY:
                URL_TO_CONNECT_TO = EASY_DATABASE_URL;
                break;
            case GamePanel.MEDIUM_DIFFICULTY:
                URL_TO_CONNECT_TO = MEDIUM_DATABASE_URL;
                break;
            case GamePanel.HARD_DIFFICULTY:
                URL_TO_CONNECT_TO = HARD_DATABASE_URL;
                break;
            default:
                throw new Exception("Not a valid difficulty was passed!");
        }

        URL url = new URL(URL_TO_CONNECT_TO);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject(lb);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        con.getInputStream().close();
    }

    public static void main(String[] args) throws Exception {
        Map<String, Integer> lb = getRemoteLeaderboard(GamePanel.EASY_DIFFICULTY);
        System.out.println("Aktuelles Leaderboard: " + lb);

        String player = "player0";
        int newScore = 17;

        if (!lb.containsKey(player) || newScore > lb.get(player)) {
            lb.put(player, newScore);
            uploadLeaderboard(lb, GamePanel.EASY_DIFFICULTY);
            System.out.println("Leaderboard aktualisiert: "+getRemoteLeaderboard(GamePanel.EASY_DIFFICULTY));
        } else {
            System.out.println("Kein neuer Highscore:");
        }

    }
}