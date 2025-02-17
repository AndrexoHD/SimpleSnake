import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class UpdateChecker {
    private static final String VERSION_URL = "https://raw.githubusercontent.com/AndrexoHD/SimpleSnake/refs/heads/main/Release/version.txt";
    private static final String CURRENT_VERSION = "1.0.0"; // current version

    public static void main(String[] args) {
        try {
            URL url = new URI(VERSION_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = reader.readLine(); // first line: version-number
            String downloadUrl = reader.readLine();  // second line: donwload-link
            reader.close();

            if (isNewerVersion(latestVersion, CURRENT_VERSION)) {
                System.out.println("Neue Version verfügbar: " + latestVersion);
                System.out.println("Download-Link: " + downloadUrl);
                // Hier könntest du eine GUI anzeigen oder automatisch herunterladen
            } else {
                System.out.println("Du hast bereits die neueste Version.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNewerVersion(String latest, String current) {
        return latest.compareTo(current) > 0; // Einfache lexikografische Versionsprüfung
    }
}
