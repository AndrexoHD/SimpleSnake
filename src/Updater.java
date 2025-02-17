import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Updater {
    private static final String TEMP_NAME = "update.jar";
    private static final String FILE_NAME = "Snake.jar";

    public Updater(String DOWNLOAD_URL) {
        try {
            downloadFile(DOWNLOAD_URL, TEMP_NAME);
            System.out.println("Update erfolgreich heruntergeladen!");
            startUpdateProcess();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String fileURL, String savePath) throws IOException, URISyntaxException {
        URL url = new URI(fileURL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private static void startUpdateProcess() throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows: Create a batch file and execute it
            String updateScript = "update.bat";
            try (PrintWriter writer = new PrintWriter(new FileWriter(updateScript))) {
                writer.println("@echo off");
                writer.println("timeout /t 2"); // Wait 2 seconds to ensure the old file is no longer locked
                writer.println("del " + FILE_NAME);
                writer.println("rename " + TEMP_NAME + " " + FILE_NAME);
                writer.println("start javaw -jar " + FILE_NAME);
                writer.println("exit");
            }
            new ProcessBuilder("cmd.exe", "/c", "start", updateScript).start();
        } else {
            // Linux/Mac: Create a shell script
            String updateScript = "update.sh";
            try (PrintWriter writer = new PrintWriter(new FileWriter(updateScript))) {
                writer.println("#!/bin/bash");
                writer.println("sleep 2"); // Wait 2 seconds
                writer.println("rm -f " + FILE_NAME);
                writer.println("mv " + TEMP_NAME + " " + FILE_NAME);
                writer.println("java -jar " + FILE_NAME + " &");
                writer.println("exit");
            }
            new ProcessBuilder("bash", updateScript).start();
        }
    }
}
