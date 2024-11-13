import java.awt.*;
import java.util.Random;

public class Food {
    private int x;
    private int y;
    private final int UNIT_SIZE = 25;
    private Random random;

    public Food() {
        random = new Random();
        respawn();
    }

    public void respawn() {
        x = random.nextInt(600 / UNIT_SIZE) * UNIT_SIZE;
        y = random.nextInt(600 / UNIT_SIZE) * UNIT_SIZE;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
