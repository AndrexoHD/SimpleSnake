import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class UpdateChecker {
    private static final String VERSION_URL = "https://raw.githubusercontent.com/AndrexoHD/SimpleSnake/refs/heads/main/Release/version.txt";
    /**
    * Current version.
    * <p>if on GitHub: MUST BE SAME as in version.txt!!!
    * <p>POSSIBLE ENDLESS RECUSION IF NOT
    */
    private static final String CURRENT_VERSION = "1.0.3";

    public UpdateChecker() {
        try {
            URL url = new URI(VERSION_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = reader.readLine(); // first line: version-number
            String downloadUrl = reader.readLine();  // second line: donwload-link
            reader.close();

            if (isNewerVersion(latestVersion, CURRENT_VERSION)) {
                System.out.println("New version available: " + latestVersion);
                System.out.println("Download-link: " + downloadUrl);
                new Updater(downloadUrl);
            } else {
                System.out.println("You already have the latest version.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNewerVersion(String latest, String current) {
        return latest.compareTo(current) > 0; // Simple lexicographical version check
    }
}
