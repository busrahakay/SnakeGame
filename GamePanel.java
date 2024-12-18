import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;


public class GamePanel extends JPanel implements Runnable { //Thread olarak çalışabilmesi sağlanır.

    private Color backgroundColor = Color.black; // Varsayılan arka plan rengi
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY_GREEN = 100;  // yeşil yılan hızı
    static final int DELAY_BLUE = 90;  //mavi yılan hızı (daha hızlı)
    static final int GAME_TIME = 15000;  //15 saniye oyun süresi

    //yılanların koordinatlarını tutmak için dizi oluşturuldu.
    final int[] greenX = new int[GAME_UNITS];
    final int[] greenY = new int[GAME_UNITS];
    final int[] blueX = new int[GAME_UNITS];
    final int[] blueY = new int[GAME_UNITS];

    //yılanların başlangıç vücut uzunlukları 
    int greenBodyParts = 3;
    int blueBodyParts = 3;

    //yılanların yedikleri elma sayıları tutulur.
    int greenApplesEaten;
    int blueApplesEaten;

    //elmanın konumları tutulur.
    int appleX;
    int appleY;

    //hareket yönleri
    char greenDirection = 'R';
    char blueDirection = 'L';

    boolean running = false;

    //THREAD KULLANIMLARI İÇİN OLUŞTURULUR.
    Thread greenThread;
    Thread blueThread;
    Thread gameThread;
    Random random;

    int timeLeft = GAME_TIME / 1000; //oyun süresi kontrol edilir. (saniye olarak)
    private JPanel endGamePanel;
    private JButton restartButton;
    private JButton exitButton;
    private boolean scoreSaved = false; 

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        createEndGamePanel(); // Bitiş panelini oluştur
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color; // Yeni rengi ayarla
        this.setBackground(color);   // Panelin arka planını güncelle
        repaint();                   // Ekranı yeniden çiz
    }
    
    private void createEndGamePanel() {
        endGamePanel = new JPanel();
        endGamePanel.setLayout(new BoxLayout(endGamePanel, BoxLayout.Y_AXIS));
        endGamePanel.setOpaque(false);
        endGamePanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 100, 100)); // Ekranın ortasında konumlandırmak için kenar boşlukları
    
        restartButton = new JButton("START AGAIN");
        restartButton.setBackground(Color.green);
        restartButton.setForeground(Color.white);
        restartButton.setPreferredSize(new Dimension(250, 70)); // Daha büyük buton boyutu
        restartButton.setFont(new Font("Ink Free", Font.BOLD, 30)); // Buton metni için yazı tipi
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Butonu ortala
        restartButton.addActionListener(e -> resetGame());
    
        exitButton = new JButton("EXIT");
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

        greenThread = new Thread(() -> {
            while (running) {
                moveSnake(greenX, greenY, greenBodyParts, greenDirection, appleX, appleY); //yılanın hareketi güncellenir.
                checkApple(greenX, greenY, "green"); //yılanın elma yiyip yememe kontrolü
                repaint(); //görsel güncelleme
                try {
                    Thread.sleep(DELAY_GREEN); //yılan hızı
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        greenThread.start();

        blueThread = new Thread(() -> {
            while (running) {
                moveSnake(blueX, blueY, blueBodyParts, blueDirection, appleX, appleY);
                checkApple(blueX, blueY, "blue");
                repaint();
                try {
                    Thread.sleep(DELAY_BLUE);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        blueThread.start();

        gameThread = new Thread(() -> { //oyun süresini kontrol eden thread
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

    public void paintComponent(Graphics g) { //swing panelinde çizim yapmak için kullanılan metottur.
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            //elma çizilir
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Green Snake çiz
            for (int i = 0; i < greenBodyParts; i++) {
                //if-else yapısıyla beraber yılanın başı ve vücudu farklı renk tonlarında ayarlandı.
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(greenX[i], greenY[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(greenX[i], greenY[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Blue Snake çiz
            for (int i = 0; i < blueBodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(0, 0, 250)); // Mavi yılanın başı için daha açık ton
                    g.fillRect(blueX[i], blueY[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(0, 0, 150)); // Mavi yılanın gövdesi için daha koyu ton
                    g.fillRect(blueX[i], blueY[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            //skorlar ve kalan süre ekrana yazdırılır.
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            g.drawString("Green Snake Score: " + greenApplesEaten, 10, g.getFont().getSize());
            g.drawString("Blue Snake Score: " + blueApplesEaten, SCREEN_WIDTH - 185, g.getFont().getSize());
            g.drawString("Time Left: " + timeLeft, SCREEN_WIDTH / 2 - 50, g.getFont().getSize());
        } 
        
        else {
            showEndScreen(g);
        }
    }

    public void newApple() { //elma için sürekli rastgele konum oluşturulur. (UNIT_SIZE yani ayarlanan karelere göre)
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
        //yılanın hareketleri güncellenir, yılanın her parçası bir öncekini takip eder.
        //yılanın ilk parçası (baş) ise elmanın konumuna doğru hareket eder.
    }

    private void checkApple(int[] x, int[] y, String snake) {
        if (x[0] == appleX && y[0] == appleY) {
            if (snake.equals("green")) {
                greenBodyParts++;
                greenApplesEaten++;
            } else if (snake.equals("blue")) {
                blueBodyParts++;
                blueApplesEaten++;
            }
            newApple();
        }
        //yılanın elmayı yiyip yemediği kontrolü yılanın başının ve elmanın konumu karşılaştırılarak yapılır.
        //buna göre yılan skoru artar ve yeni rastgele konumlu bir elma yeniden oluşturulur.
    }

    private void showEndScreen(Graphics g) {
        if (!scoreSaved) {
            // Skorları veri tabanına kaydet
            DatabaseHelper.saveScore(greenApplesEaten, blueApplesEaten);
            scoreSaved = true;  // Skor kaydedildiği güncelleme yap.
        }
    
        //kazanan belirlenir.
        g.setColor(Color.red);
        g.setFont(new Font("Giddyup Std", Font.PLAIN, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String message = greenApplesEaten > blueApplesEaten ? "Green Snake Wins!" : "Blue Snake Wins!";
        if (greenApplesEaten == blueApplesEaten) {
            message = "Draw!";
        }
        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, SCREEN_HEIGHT / 2 - 30);

        // Son 3 skoru getir
        List<String> lastScores = DatabaseHelper.getLastThreeScores();
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        int yOffset = SCREEN_HEIGHT / 2 + 40;
        g.drawString("Last 3 Scores", (SCREEN_WIDTH - metrics.stringWidth("Last 3 Scores:")) / 2 + 25, yOffset);
       
        for (String score : lastScores) {
            yOffset += 40;
            g.drawString(score, (SCREEN_WIDTH - metrics.stringWidth(score)) / 2 + 25, yOffset);
        }

        // Bitiş panelini görünür yap
        endGamePanel.setVisible(true);
        this.add(endGamePanel, BorderLayout.CENTER);  //paneli ekrana ekle
        this.revalidate(); //bileşenleri yeniden yerleştir
        this.repaint(); //yeniden çiz
    }

    private void endGame() { //exit durumundan sonra her şey durdurularak kapatılır
        running = false;
        repaint();

        // Thread'leri durdur
        try {
            greenThread.join();
            blueThread.join();
            gameThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void resetGame() { //start again durumunda oyun tekrar çağırılmadan önce tüm parametreler sıfırlanır
        greenBodyParts = 3;
        blueBodyParts = 3;
        greenApplesEaten = 0;
        blueApplesEaten = 0;
        timeLeft = GAME_TIME / 1000;
        running = true;
        scoreSaved = false;  // Kaydetme sıfırlandı
    
        for (int i = 0; i < greenX.length; i++) {
            greenX[i] = 0;
            greenY[i] = 0;
            blueX[i] = 0;
            blueY[i] = 0;
        }
    
        remove(endGamePanel); // Bitiş panelini kaldır
        startGame(); // Oyunu yeniden başlat
    }
    
    @Override
    public void run() {
        // Thread işlevselliği burada yer alıyor.
    }
}