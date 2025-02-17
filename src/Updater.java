import java.io.File;
import java.io.FileOutputStream;
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
            File updateFile = new File(updateScript);
            try (PrintWriter writer = new PrintWriter(updateFile)) {
                writer.println("@echo off");
                writer.println("echo Updating to new version... please wait");
                writer.println("timeout /t 3"); // Wait 3 seconds to ensure the old file is no longer locked
                writer.println("del " + FILE_NAME);
                writer.println("rename " + TEMP_NAME + " " + FILE_NAME);
                writer.println("start javaw -jar " + FILE_NAME);
                writer.println("start /b cmd /c \"timeout /t 1 >nul & del /f /q update.bat\"");
                writer.println("exit");
            }
            new ProcessBuilder("cmd.exe", "/c", "start", updateScript).start();
        } else {
            // Linux/Mac: Create a shell script
            String updateScript = "update.sh";
            File updateFile = new File(updateScript);
            try (PrintWriter writer = new PrintWriter(updateFile)) {
                writer.println("#!/bin/bash");
                writer.println("echo Updating to new version... please wait");
                writer.println("sleep 3");
                writer.println("rm -f " + FILE_NAME);
                writer.println("mv " + TEMP_NAME + " " + FILE_NAME);
                writer.println("java -jar " + FILE_NAME + " &");
                writer.println("exit");
            }
            new ProcessBuilder("bash", updateScript).start();
            // delete the update.sh file
            new ProcessBuilder("bash", "-c", "sleep 1 && rm -- update.sh").start();


        }
    }
}
