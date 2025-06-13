import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONObject;

public class FirebaseLeaderboard {

    private static final String EASY_DATABASE_URL = "https://andrexo-snake-leaderboard-default-rtdb.europe-west1.firebasedatabase.app/easyLeaderboard.json";
    private static final String MEDIUM_DATABASE_URL = "https://andrexo-snake-leaderboard-default-rtdb.europe-west1.firebasedatabase.app/leaderboard.json";
    private static final String HARD_DATABASE_URL = "https://andrexo-snake-leaderboard-default-rtdb.europe-west1.firebasedatabase.app/hardLeaderboard.json";

    public static Map<String, Integer> getLeaderboard() throws IOException {
        URL url = new URL(MEDIUM_DATABASE_URL);
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
        if (!response.toString().equals("null")) {
            JSONObject json = new JSONObject(response.toString());
            for (String key : json.keySet()) {
                leaderboard.put(key, json.getInt(key));
                System.out.println("Aktueller Key: " + key);
            }
        }
        return leaderboard;
    }

    public static void uploadLeaderboard(Map<String, Integer> leaderboard) throws IOException {
        URL url = new URL(MEDIUM_DATABASE_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject(leaderboard);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = json.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        con.getInputStream().close();
    }

    public static void main(String[] args) throws IOException {
        Map<String, Integer> lb = getLeaderboard();
        System.out.println("Aktuelles Leaderboard: " + lb);

        String player = "player1";
        int newScore = 70;

        if (!lb.containsKey(player) || newScore > lb.get(player)) {
            lb.put(player, newScore);
            uploadLeaderboard(lb);
            System.out.println("Leaderboard aktualisiert.");
        } else {
            System.out.println("Kein neuer Highscore.");
        }
    }
}