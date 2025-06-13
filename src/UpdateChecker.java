import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class UpdateChecker {
    private static final String VERSION_URL = "https://raw.githubusercontent.com/AndrexoHD/SimpleSnake/refs/heads/main/Release/version.txt";
    /**
    * Current version.
    * <p>If on GitHub: <b>MUST BE SAME</b> as in version.txt!</p>
    * <p>!!! POSSIBLE ENDLESS RECUSION IF NOT !!!</p>
    */
    private static final String CURRENT_VERSION = "1.3.1";

    public UpdateChecker() {
        try {
            URL url = new URI(VERSION_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = reader.readLine(); // first line: version-number
            String downloadUrl = reader.readLine();  // second line: donwload-link
            reader.close();

            if(isNewerVersion(latestVersion, CURRENT_VERSION)) {
                System.out.println("New version available: " + latestVersion);
                System.out.println("Download-link: " + downloadUrl);
                // ask user in a small window if they want to update
                new UpdatePrompt(downloadUrl);
            } else {
                System.out.println("You already have the latest version.");
                new GameFrame();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNewerVersion(String latest, String current) {
        return latest.compareTo(current) > 0; // Simple lexicographical version check
    }
}

class UpdatePrompt extends JPanel {
    private final static int WIDTH = 300;
    private final static int HEIGHT = 200;

    public UpdatePrompt(String downloadUrl) {
        JFrame askUpdate = new JFrame();
        askUpdate.setTitle("Update?");
        askUpdate.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        askUpdate.setResizable(false);
        askUpdate.setSize(WIDTH, HEIGHT);
        askUpdate.setAlwaysOnTop(true);
        askUpdate.setVisible(true);
        askUpdate.setLocationRelativeTo(null);
        askUpdate.add(this);
        Container contentPane = this;
        contentPane.setLayout(new GridLayout(2,1));
        Container empty = new Container();
        Container buttons = new Container();
        buttons.setLayout(new GridLayout(1, 2));
        Font buttonFont = new Font("Arial", Font.BOLD, 30);
        JButton yesButton = new JButton("Yes");
        yesButton.setFont(buttonFont);
        buttons.add(yesButton);
        yesButton.addActionListener((e) -> new Updater(downloadUrl));
        JButton noButton = new JButton("No");
        noButton.setFont(buttonFont);
        buttons.add(noButton);
        noButton.addActionListener((e) -> {
            askUpdate.dispose();
            new GameFrame();
        });
        contentPane.add(empty);
        contentPane.add(buttons);
    }

    public void draw(Graphics g) {
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics metrics = g.getFontMetrics();
        g.drawString("A new update is available.", (WIDTH - metrics.stringWidth("A new update is available."))/2, 30);
        g.drawString("Do you want to update?", (WIDTH - metrics.stringWidth("Do you want to update?"))/2, 60);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
}
