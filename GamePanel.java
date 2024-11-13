import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private Snake snake1;
    private Snake snake2;
    private Food food;
    private int gameTime = 15;
    private Timer gameTimer;

    public GamePanel() {
        this.setPreferredSize(new Dimension(600, 600));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);

        // Yılanların farklı başlangıç pozisyonları
        snake1 = new Snake(Color.GREEN, 100, 100);
        snake2 = new Snake(Color.BLUE, 400, 400);
        food = new Food();

        new Thread(snake1).start();
        new Thread(snake2).start();

        // Oyun süresi 15 saniye
        gameTimer = new Timer(1000, e -> {
            gameTime--;
            if (gameTime <= 0) {
                endGame();
            }
            repaint();
        });
        gameTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        snake1.draw(g);
        snake2.draw(g);
        food.draw(g);

        // Skor ve zaman
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 20));
        g.drawString("Green Snake Score: " + snake1.getScore(), 10, 20);
        g.drawString("Blue Snake Score: " + snake2.getScore(), 10, 40);
        g.drawString("Time Left: " + gameTime, 400, 20);
    }

    // Oyun döngüsü
    public void gameLoop() {
        while (gameTime > 0) {
            snake1.moveTowardsFood(food.getX(), food.getY());
            snake2.moveTowardsFood(food.getX(), food.getY());

            checkCollision();
            repaint();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkCollision() {
        // Yılanların yiyeceğe ulaşması durumu
        if (snake1.getHeadX() == food.getX() && snake1.getHeadY() == food.getY()) {
            snake1.grow();  // Yılan büyüsün
            food.respawn();
        }
        if (snake2.getHeadX() == food.getX() && snake2.getHeadY() == food.getY()) {
            snake2.grow();  // Yılan büyüsün
            food.respawn();
        }

        // Yılanlar çarpıştığında hızlarına göre kazananı belirle
        if (snake1.getHeadX() == snake2.getHeadX() && snake1.getHeadY() == snake2.getHeadY()) {
            if (snake1.getSpeed() < snake2.getSpeed()) {
                snake1.grow();  // Hızlı olan kazanır
                snake2.stop();
                snake2 = new Snake(Color.BLUE, 400, 400);  // Yeniden başlat
                new Thread(snake2).start();
            } else {
                snake2.grow();
                snake1.stop();
                snake1 = new Snake(Color.GREEN, 100, 100);  // Yeniden başlat
                new Thread(snake1).start();
            }
        }
    }

    private void endGame() {
        gameTimer.stop();
        String winner;
        if (snake1.getScore() > snake2.getScore()) {
            winner = "Snake 1 Wins!";
        } else if (snake2.getScore() > snake1.getScore()) {
            winner = "Snake 2 Wins!";
        } else {
            winner = "It's a Draw!";
        }

        JOptionPane.showMessageDialog(this, winner, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}
