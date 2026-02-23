package andrexohd.snakegame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@SuppressWarnings("deprecation")
public class JSONLeaderboard {

    // if you want to host your own JSON Realtime Database then fill in these URLs
    private static final String EASY_DATABASE_URL = "";
    private static final String MEDIUM_DATABASE_URL = "";
    private static final String HARD_DATABASE_URL = "";

    public static int score = 0;

    public static Map<String, Integer> getRemoteLeaderboard(String difficulty) throws Exception {
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
                throw new Exception("A non-valid difficulty was passed to getRemoteLeaderboard()");
        }

        try {
            URL url = new URL(URL_TO_CONNECT_TO);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(60000);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            Map<String, Integer> leaderboard = new HashMap<>();
            String responseStr = response.toString().trim();
            if (!responseStr.equals(null) && !responseStr.isEmpty() && responseStr.startsWith("{")) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(responseStr, JsonObject.class);
                for (String key : json.keySet()) {
                    leaderboard.put(key, json.get(key).getAsInt());
                }
            }
            // sortieren
            return leaderboard;
        } catch (Exception e) {
            e.printStackTrace();
            // no internet connection or database URL is invalid, then return null and keep the programm running without a leaderboard
            return null;
        }
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

        try {
            URL url = new URL(URL_TO_CONNECT_TO);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(60000);

            Gson gson = new Gson();
            String json = gson.toJson(lb);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            con.getInputStream().close();
        } catch (Exception e) {
            // no internet connection or database URL is invalid, then keep the programm running without a leaderboard
        }
    }
}