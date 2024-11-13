import java.awt.*;
import java.util.Random;

public class Snake implements Runnable {
    private int[] x;
    private int[] y;
    private int bodyParts;
    private final int UNIT_SIZE = 25;
    private final int MAX_PARTS = 600;
    private boolean running = true;
    private final Color color;
    private Random random;
    private int score = 0;
    private int speed = 100;

    public Snake(Color color, int startX, int startY) {
        x = new int[MAX_PARTS];
        y = new int[MAX_PARTS];
        bodyParts = 5;
        this.color = color;
        random = new Random();

        // Başlangıç pozisyonlarını güncelliyoruz
        for (int i = 0; i < bodyParts; i++) {
            x[i] = startX - i * UNIT_SIZE;
            y[i] = startY;
        }
    }

    public void moveTowardsFood(int foodX, int foodY) {
        // Gövde parçalarının yerini güncelle (yılanın tam hareketi için)
        for (int i = bodyParts - 1; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Baş kısmını yiyeceğe doğru hareket ettir
        int xOffset = random.nextInt(3) - 1;
        int yOffset = random.nextInt(3) - 1;

        if (x[0] < foodX) {
            x[0] += UNIT_SIZE + xOffset;
        } else if (x[0] > foodX) {
            x[0] -= UNIT_SIZE + xOffset;
        }

        if (y[0] < foodY) {
            y[0] += UNIT_SIZE + yOffset;
        } else if (y[0] > foodY) {
            y[0] -= UNIT_SIZE + yOffset;
        }

        // Duvarlardan geçiş (yılan duvarlara çarpmaz)
        if (x[0] < 0) x[0] = 600 - UNIT_SIZE;
        if (x[0] >= 600) x[0] = 0;
        if (y[0] < 0) y[0] = 600 - UNIT_SIZE;
        if (y[0] >= 600) y[0] = 0;
    }

    public void grow() {
        bodyParts++;  // Boyutu artır
        score++;      // Skoru artır
    }

    public void draw(Graphics g) {
        g.setColor(color);
        for (int i = 0; i < bodyParts; i++) {
            g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
    }

    public int getScore() {
        return score;
    }

    public int getHeadX() {
        return x[0];
    }

    public int getHeadY() {
        return y[0];
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }
}
