import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY_PLAYER = 100;  // Oyuncu yılan hızı
    static final int DELAY_AI = 90;      // AI yılan hızı (daha hızlı)
    static final int GAME_TIME = 15000;  // 15 saniye oyun süresi
    final int[] playerX = new int[GAME_UNITS];
    final int[] playerY = new int[GAME_UNITS];
    final int[] aiX = new int[GAME_UNITS];
    final int[] aiY = new int[GAME_UNITS];
    int playerBodyParts = 3;
    int aiBodyParts = 3;
    int playerApplesEaten;
    int aiApplesEaten;
    int appleX;
    int appleY;
    char playerDirection = 'R';
    char aiDirection = 'L';
    boolean running = false;
    Thread playerThread;
    Thread aiThread;
    Thread gameThread;
    Random random;

    int timeLeft = GAME_TIME / 1000; // Saniye sayacı
    private JPanel endGamePanel;
    private JButton restartButton;
    private JButton exitButton;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        
        createEndGamePanel(); // Bitiş panelini oluştur
    }

    private void createEndGamePanel() {
        endGamePanel = new JPanel();
        endGamePanel.setLayout(new BoxLayout(endGamePanel, BoxLayout.Y_AXIS));
        endGamePanel.setBackground(Color.black);
        endGamePanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100)); // Ekranın ortasında konumlandırmak için kenar boşlukları
    
        restartButton = new JButton("Tekrar Oyna");
        restartButton.setBackground(Color.green);
        restartButton.setForeground(Color.white);
        restartButton.setPreferredSize(new Dimension(250, 70)); // Daha büyük buton boyutu
        restartButton.setFont(new Font("Ink Free", Font.BOLD, 30)); // Buton metni için yazı tipi
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Butonu ortala
        restartButton.addActionListener(e -> resetGame());
    
        exitButton = new JButton("Çık");
        exitButton.setBackground(Color.red);
        exitButton.setForeground(Color.white);
        exitButton.setPreferredSize(new Dimension(250, 70)); // Daha büyük buton boyutu
        exitButton.setFont(new Font("Ink Free", Font.BOLD, 30)); // Buton metni için yazı tipi
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Butonu ortala
    
        exitButton.addActionListener(e -> System.exit(0));
    
        endGamePanel.add(Box.createRigidArea(new Dimension(0, 20))); // Butonlar arasına boşluk
        endGamePanel.add(restartButton);
        endGamePanel.add(Box.createRigidArea(new Dimension(0, 20))); // Butonlar arasına boşluk
        endGamePanel.add(exitButton);
        endGamePanel.setVisible(false); // İlk başta görünmez yap
    }
    
    public void startGame() {
        newApple();
        running = true;

        playerThread = new Thread(() -> {
            while (running) {
                moveSnake(playerX, playerY, playerBodyParts, playerDirection, appleX, appleY);
                checkApple(playerX, playerY, "player");
                repaint();
                try {
                    Thread.sleep(DELAY_PLAYER);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        playerThread.start();

        aiThread = new Thread(() -> {
            while (running) {
                moveSnake(aiX, aiY, aiBodyParts, aiDirection, appleX, appleY);
                checkApple(aiX, aiY, "ai");
                repaint();
                try {
                    Thread.sleep(DELAY_AI);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        aiThread.start();

        gameThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            while (timeLeft > 0 && running) {
                long currentTime = System.currentTimeMillis();
                
                // Eğer 1 saniye geçtiyse süreyi güncelle
                if (currentTime - startTime >= 1000) {
                    timeLeft--;
                    startTime = currentTime; // başlangıcı güncelle
                }
                
                try {
                    Thread.sleep(50); // Daha sık kontrol yaparak hassasiyeti artır
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            endGame();
        });
        gameThread.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Green Snake çiz
            for (int i = 0; i < playerBodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(playerX[i], playerY[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(playerX[i], playerY[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Blue Snake çiz
            for (int i = 0; i < aiBodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.blue);
                    g.fillRect(aiX[i], aiY[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(aiX[i], aiY[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            g.drawString("Green Snake Score: " + playerApplesEaten, 10, g.getFont().getSize());
            g.drawString("Blue Snake Score: " + aiApplesEaten, SCREEN_WIDTH - 185, g.getFont().getSize());
            g.drawString("Time Left: " + timeLeft, SCREEN_WIDTH / 2 - 50, g.getFont().getSize());
        } else {
            showEndScreen(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void moveSnake(int[] x, int[] y, int bodyParts, char direction, int targetX, int targetY) {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (x[0] < targetX) {
            direction = 'R';
        } else if (x[0] > targetX) {
            direction = 'L';
        } else if (y[0] < targetY) {
            direction = 'D';
        } else if (y[0] > targetY) {
            direction = 'U';
        }
        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE;
            case 'D' -> y[0] += UNIT_SIZE;
            case 'L' -> x[0] -= UNIT_SIZE;
            case 'R' -> x[0] += UNIT_SIZE;
        }
    }

    private void checkApple(int[] x, int[] y, String snake) {
        if (x[0] == appleX && y[0] == appleY) {
            if (snake.equals("player")) {
                playerBodyParts++;
                playerApplesEaten++;
            } else if (snake.equals("ai")) {
                aiBodyParts++;
                aiApplesEaten++;
            }
            newApple();
        }
    }

    private void endGame() {
        running = false;
        removeAll(); // Tüm bileşenleri kaldır
        add(endGamePanel); // Bitiş panelini ekle
        endGamePanel.setVisible(true); // Bitiş panelini görünür yap
        repaint();
        try {
            playerThread.join();
            aiThread.join();
            gameThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void showEndScreen(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String message = playerApplesEaten > aiApplesEaten ? "Green Snake Wins!" : "Blue Snake Wins!";
        if (playerApplesEaten == aiApplesEaten) {
            message = "Scoreless!";
        }
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, SCREEN_HEIGHT / 2 - 20);
    }

    public void resetGame() {
        playerBodyParts = 3;
        aiBodyParts = 3;
        playerApplesEaten = 0;
        aiApplesEaten = 0;
        timeLeft = GAME_TIME / 1000;
        running = true;

        for (int i = 0; i < playerX.length; i++) {
            playerX[i] = 0;
            playerY[i] = 0;
            aiX[i] = 0;
            aiY[i] = 0;
        }

        remove(endGamePanel); // Bitiş panelini kaldır
        startGame();
    }
    @Override
    public void run() {
        // Thread işlevselliği burada yer alıyor
    }
}