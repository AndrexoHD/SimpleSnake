import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.*;

import javax.swing.*;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class GamePanel extends JPanel implements ActionListener{
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int LEADERBOARD_ENTRIES = 10;
    static int DELAY = 100;
    static String difficulty = "Medium";
    public static final String EASY_DIFFICULTY = "Easy";
    public static final String MEDIUM_DIFFICULTY = "Medium";
    public static final String HARD_DIFFICULTY = "Hard";
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean startScreen = true;
    boolean running = false;
    boolean inputRecieved = false;

    /** Used if a second input occurs mid-frame. Ensures smoother gameplay.*/
    Character secondInput = null;

    boolean drawGrid = false;
    boolean pause = false;
    Timer timer;
    Random random;
    HighscoreManager highscoreManager;

    GamePanel() {
        random = new Random();
        highscoreManager = new HighscoreManager();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        if (highscoreManager.readUsername().trim().isEmpty()) {
            changeUsernamePrompt();
        }
        startGame();
    }

    public void startGame() {
        if(!startScreen) {
            newApple();
            timer = new Timer(DELAY, this);
            timer.start();
            running = true;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            draw(g);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) throws Exception {
        if(startScreen) {
            homeMenu(g);
        } else if(running) {
            if(drawGrid) {
                for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
                    g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT); // vertical lines
                    g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE); // horizontal lines
                }
            }
            // check if spawned apple is inside snake
            g.setColor(Color.red);
            for (int i = bodyParts; i > 0; i--) {
                if(x[i] == appleX && y[i] == appleY) newApple();
            }
            // draw apple
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            // draw snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // head
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else { // body
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // checks for body collisions
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }

        // check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // check if head touches right border; -1 because clipping for some reason
        if (x[0] > SCREEN_WIDTH - 1) {
            running = false;
        }
        // check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // check if head touches bottom border; -1 because clipping for some reason
        if (y[0] > SCREEN_HEIGHT - 1) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    /**
     * The inside of this method defines the contents of the home menu.
     * @param g
     */
    public void homeMenu(Graphics g) {
        // title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.PLAIN, 70));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("SimpleSnake", (SCREEN_WIDTH - metrics.stringWidth("SimpleSnake"))/2, SCREEN_HEIGHT/10*1);
        // difficulty
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 40));
        switch (difficulty) {
            case EASY_DIFFICULTY:
                g.setColor(Color.GREEN);
                break;
            case MEDIUM_DIFFICULTY:
                g.setColor(Color.YELLOW);
                break;
            case HARD_DIFFICULTY:
                g.setColor(Color.RED);
                break;
            default:
                g.setColor(Color.LIGHT_GRAY);
                break;
        }
        metrics = getFontMetrics(g.getFont());
        g.drawString("Difficulty: " + difficulty, (SCREEN_WIDTH - metrics.stringWidth("Difficulty: " + difficulty))/2, SCREEN_HEIGHT/10*2);
        // username button
        JButton usernameButton = new JButton("<html><center>Change Username<br>[U]</center></html>");
        usernameButton.setBounds((SCREEN_WIDTH-175)/2, SCREEN_HEIGHT-175, 175, 50);
        usernameButton.setFont(new Font("Arial", Font.BOLD, 15));
        usernameButton.setFocusable(false);
        this.add(usernameButton);
        usernameButton.addActionListener((e) -> {
            changeUsernamePrompt();
        });
        // username
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 40));
        g.setColor(new Color(90, 250, 135));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Username: " + highscoreManager.readUsername(),
        (SCREEN_WIDTH - metrics.stringWidth("Username: " + highscoreManager.readUsername()))/2,
        SCREEN_HEIGHT-190);
        // easy button
        JButton easyButton = new JButton("<html><center>Easy<br>[1]</center></html>");
        easyButton.setBounds(50, SCREEN_HEIGHT/20*5, 150, 75);
        easyButton.setFont(new Font("Arial", Font.BOLD, 30));
        easyButton.setFocusable(false);
        this.add(easyButton);
        easyButton.addActionListener((e) -> {
            DELAY = 250;
            difficulty = EASY_DIFFICULTY;
            repaint();
        });
        // medium button
        JButton mediumButton = new JButton("<html><center>Medium<br>[2]</center></html>");
        mediumButton.setBounds((SCREEN_WIDTH-150)/2, SCREEN_HEIGHT/20*5, 150, 75);
        mediumButton.setFont(new Font("Arial", Font.BOLD, 25));
        mediumButton.setFocusable(false);
        this.add(mediumButton);
        mediumButton.addActionListener((e) -> {
            DELAY = 100;
            difficulty = MEDIUM_DIFFICULTY;
            repaint();
        });
        // hard button
        JButton hardButton = new JButton("<html><center>Hard<br>[3]</center></html>");
        hardButton.setBounds((SCREEN_WIDTH-100)-100, SCREEN_HEIGHT/20*5, 150, 75);
        hardButton.setFont(new Font("Arial", Font.BOLD, 25));
        hardButton.setFocusable(false);
        this.add(hardButton);
        hardButton.addActionListener((e) -> {
            DELAY = 50;
            difficulty = HARD_DIFFICULTY;
            repaint();
        });
        // controls
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 25));
        metrics = getFontMetrics(g.getFont());
        String controls = "Controls:\n\nWASD/↑↓→←:move\nSpace:start/pause game\nESC:close game";
        String[] lines = controls.split("\n");
        int y = (int)(SCREEN_HEIGHT/20*8.5);
        for (String line : lines) {
            line = printControlLine(line);
            g.drawString(line, (SCREEN_WIDTH-metrics.stringWidth(line))/2, y);
            y += g.getFont().getSize();
        }
        // start game button
        this.setLayout(null);
        JButton startGameButton = new JButton("<html><center>Start Game<br>[Space]</center></html>");
        startGameButton.setBounds((SCREEN_WIDTH-200)/2, SCREEN_HEIGHT-100, 200, 80);
        startGameButton.setFont(new Font("Arial", Font.BOLD, 30));
        this.add(startGameButton);
        startGameButton.addActionListener((e) -> {
            startFromHome();
        });
    }

    public String printControlLine(String line) {
        if (line.equals("Controls:") || line.trim().isEmpty()) {
            return line;
        }
        String[] split = line.split(":");
        split[0] += ":";
        for (int i = 0; i < 35-line.length(); i++) {
            split[0] += ".";
        }
        return split[0]+split[1];
    }

    /** This method only gets called from homeMenu()*/
    public void startFromHome() {
        this.removeAll();
        startScreen = false;
        startGame();
    }

    public void changeUsernamePrompt() {
        String result = "";
        do {
            result = (String)JOptionPane.showInputDialog(
                this,
                "Select a username - cannot be more than 20 characters.",
                "Username",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                highscoreManager.readUsername()
            );
        } while (result.length() <= 0 && result.length() > 20);
        if(result != null && result.length() > 0) highscoreManager.newUsername(result);
        repaint();
    }

    public void gameOver(Graphics g) throws Exception {
        // erase snake from screen
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        // game over text
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.setColor(Color.red);
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/10);
        boolean displayHint = true; // for displaying a hint, if no new highscore has been achieved

        // highscore logic + show local highscore
        Map<String, Integer> lbMap = JSONLeaderboard.getRemoteLeaderboard(difficulty); // is null if database URL is empty.
        // following code is designed to run flawlessly even if no remote leaderboard is present
        // may be messy, but it works and I don't want to refactor this mess. I already spent too much hours on this.
        switch(difficulty) {
            case EASY_DIFFICULTY:
                if (applesEaten > highscoreManager.readEasyHighscore()) { // new easy highscore
                    highscoreManager.newEasyHighscore(applesEaten);
                    g.setColor(Color.GREEN);
                    g.setFont(new Font("Arial", Font.BOLD, 55));
                    metrics = getFontMetrics(g.getFont());
                    g.drawString("NEW EASY", (SCREEN_WIDTH - metrics.stringWidth("NEW EASY"))/2, SCREEN_HEIGHT/10*2);
                    g.drawString("HIGHSCORE: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("HIGHSCORE: " + applesEaten))/2, SCREEN_HEIGHT/10*2 + g.getFont().getSize());
                    displayHint = false;
                } else {
                    g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, SCREEN_HEIGHT/10*2);
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawString("Easy Highscore: " + highscoreManager.readEasyHighscore(), (SCREEN_WIDTH - metrics.stringWidth("Easy Highscore: " + highscoreManager.readEasyHighscore()))/2, SCREEN_HEIGHT/10*3);
                }
                // upload user if username not present
                if (lbMap != null && !lbMap.containsKey(highscoreManager.readUsername())) {
                    lbMap.put(highscoreManager.readUsername(), highscoreManager.readEasyHighscore());
                    JSONLeaderboard.uploadLeaderboard(lbMap, difficulty);
                }
                // upload new highscore to remote easy leaderboard
                if (lbMap != null && highscoreManager.readEasyHighscore() > lbMap.get(highscoreManager.readUsername())) {
                    lbMap.put(highscoreManager.readUsername(), highscoreManager.readEasyHighscore());
                    JSONLeaderboard.uploadLeaderboard(lbMap, difficulty);
                }
                break;
            case MEDIUM_DIFFICULTY:
                if (applesEaten > highscoreManager.readMediumHighscore()) { // new medium highscore
                    highscoreManager.newMediumHighscore(applesEaten);
                    g.setColor(Color.GREEN);
                    g.setFont(new Font("Arial", Font.BOLD, 55));
                    metrics = getFontMetrics(g.getFont());
                    g.drawString("NEW MEDIUM", (SCREEN_WIDTH - metrics.stringWidth("NEW MEDIUM"))/2, SCREEN_HEIGHT/10*2);
                    g.drawString("HIGHSCORE: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("HIGHSCORE: " + applesEaten))/2, SCREEN_HEIGHT/10*2 + g.getFont().getSize());
                    displayHint = false;
                } else {
                    g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, SCREEN_HEIGHT/10*2);
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawString("Medium Highscore: " + highscoreManager.readMediumHighscore(), (SCREEN_WIDTH - metrics.stringWidth("Medium Highscore: " + highscoreManager.readMediumHighscore()))/2, SCREEN_HEIGHT/10*3);
                }
                // upload user if username not present
                if (lbMap != null && !lbMap.containsKey(highscoreManager.readUsername())) {
                    lbMap.put(highscoreManager.readUsername(), highscoreManager.readMediumHighscore());
                    JSONLeaderboard.uploadLeaderboard(lbMap, difficulty);
                }
                // upload new highscore to remote medium leaderboard
                if (lbMap != null && highscoreManager.readMediumHighscore() > lbMap.get(highscoreManager.readUsername())) {
                    lbMap.put(highscoreManager.readUsername(), highscoreManager.readMediumHighscore());
                    JSONLeaderboard.uploadLeaderboard(lbMap, difficulty);
                }
                break;
            case HARD_DIFFICULTY:
                if (applesEaten > highscoreManager.readHardHighscore()) { // new hard highscore
                    highscoreManager.newHardHighscore(applesEaten);
                    g.setColor(Color.GREEN);
                    g.setFont(new Font("Arial", Font.BOLD, 55));
                    metrics = getFontMetrics(g.getFont());
                    g.drawString("NEW HARD", (SCREEN_WIDTH - metrics.stringWidth("NEW HARD"))/2, SCREEN_HEIGHT/10*2);
                    g.drawString("HIGHSCORE: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("HIGHSCORE: " + applesEaten))/2, SCREEN_HEIGHT/10*2 + g.getFont().getSize());
                    displayHint = false;
                } else {
                    g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, SCREEN_HEIGHT/10*2);
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawString("Hard Highscore: " + highscoreManager.readHardHighscore(), (SCREEN_WIDTH - metrics.stringWidth("Hard Highscore: " + highscoreManager.readHardHighscore()))/2, SCREEN_HEIGHT/10*3);
                }
                // upload user if username not present
                if (lbMap != null && !lbMap.containsKey(highscoreManager.readUsername())) {
                    lbMap.put(highscoreManager.readUsername(), highscoreManager.readHardHighscore());
                    JSONLeaderboard.uploadLeaderboard(lbMap, difficulty);
                }
                // upload new highscore to remote hard leaderboard
                if (lbMap != null && highscoreManager.readHardHighscore() > lbMap.get(highscoreManager.readUsername())) {
                    lbMap.put(highscoreManager.readUsername(), highscoreManager.readHardHighscore());
                    JSONLeaderboard.uploadLeaderboard(lbMap, difficulty);
                }
                break;
        }
        // show remote leaderboard if present
        if (lbMap != null) printRemoteLeaderboard(g, lbMap, difficulty);

        // hint for toggling grid
        if (displayHint) {
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.LIGHT_GRAY);
            metrics = getFontMetrics(g.getFont());
            g.drawString("Hint: Press G to toggle grid", (SCREEN_WIDTH - metrics.stringWidth("Hint: Press G to toggle grid"))/2, SCREEN_HEIGHT/20*17);
        }
        // restart button
        JButton restartButton = new JButton("Restart [R]");
        restartButton.setBounds((SCREEN_WIDTH-200)/2, SCREEN_HEIGHT-75, 200, 50);
        restartButton.setFont(new Font("Arial", Font.BOLD, 30));
        this.add(restartButton);
        restartButton.addActionListener((e) -> {
            resetGame();
        });
        // back to home menu button
        JButton homeButton = new JButton("<html><center>Home<br>[Space]</center></html>");
        homeButton.setBounds(20, SCREEN_HEIGHT-75, 110, 50);
        homeButton.setFont(new Font("Arial", Font.BOLD, 18));
        this.add(homeButton);
        homeButton.addActionListener((e) -> backToHome());
    }

    public void printRemoteLeaderboard(Graphics g, Map<String, Integer> leaderboard, String difficulty) throws Exception {
        Map<String, Integer> result = new TreeMap<>((a, b) -> {
            Integer va = leaderboard.get(a);
            Integer vb = leaderboard.get(b);
            if (va == null && vb == null) return a.compareTo(b);
            if (va == null) return 1;
            if (vb == null) return -1;
            if (va == vb) return  1;
            return vb - va;
        });
        result.putAll(leaderboard);
        // System.out.println(result);
        g.setColor(new Color(90, 250, 135));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        StringBuilder leaderboardText = new StringBuilder("Leaderboard ("+difficulty+" difficulty):\n");
        int rank = 1;
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            if (rank>LEADERBOARD_ENTRIES) {
                break;
            }
            String line = new Entry(entry.getKey(), entry.getValue()).printAsLine();
            if (rank == 10) {
                line = String.format("%-2d. %s", rank, line+"\n");
            } else {
                line = String.format("%d.  %s", rank, line+"\n");
            }
            leaderboardText.append(line);
            // System.out.println("Eintrag "+rank+": "+entry);
            rank++;
        }
        String[] lines = leaderboardText.toString().split("\n");
        int y = SCREEN_HEIGHT/20*7;
        for (String line : lines) {
            g.drawString(line, (SCREEN_WIDTH-metrics.stringWidth(line))/2, y);
            y += metrics.getHeight();
        }
    }

    public void backToHome() {
        this.removeAll();
        startScreen = true;
        running = false;
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        x[0] = 0;
        y[0] = 0;
        repaint();
    }

    public void resetGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        x[0] = 0;
        y[0] = 0;
        running = true;
        this.removeAll();
        startGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        inputRecieved = false;
        if(running) {
            move();
            checkApple();
            checkCollisions();
            if (secondInput != null) {
                direction = secondInput;
                secondInput = null;
            }
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if(inputRecieved) secondInput = 'L';
                    else if (direction != 'R' && !pause) {
                        direction = 'L';
                        inputRecieved = true;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if(inputRecieved) secondInput = 'R';
                    else if (direction != 'L' && !pause) {
                        direction = 'R';
                        inputRecieved = true;
                    }
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if(inputRecieved) secondInput = 'U';
                    else if (direction != 'D' && !pause) {
                        direction = 'U';
                        inputRecieved = true;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if(inputRecieved) secondInput = 'D';
                    else if (direction != 'U' && !pause) {
                        direction = 'D';
                        inputRecieved = true;
                    }
                    break;
                case KeyEvent.VK_R:
                    if (!running) {
                        resetGame();
                    }
                    break;
                case KeyEvent.VK_U:
                    if(startScreen) {
                        changeUsernamePrompt();
                    }
                    break;
                case KeyEvent.VK_G:
                    drawGrid = !drawGrid;
                    if(pause) repaint();
                    break;
                case KeyEvent.VK_SPACE:
                    if (running) {
                        if (!pause) {
                            pause = true;
                            timer.stop();
                        } else {
                            pause = false;
                            timer.start();
                        }
                    } else if(startScreen) {
                        startFromHome();
                    } else {
                        backToHome();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
                case KeyEvent.VK_1:
                    if (startScreen) {
                        DELAY = 250;
                        difficulty = EASY_DIFFICULTY;
                        repaint();
                        break;
                    }
                case KeyEvent.VK_2:
                    if (startScreen) {
                        DELAY = 100;
                        difficulty = MEDIUM_DIFFICULTY;
                        repaint();
                        break;
                    }
                case KeyEvent.VK_3:
                    if (startScreen) {
                        DELAY = 50;
                        difficulty = HARD_DIFFICULTY;
                        repaint();
                        break;
                    }
            }
        }
    }
}
