import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        this.add(new GamePanel());
        this.setTitle("Yılan Savaşı");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        GameFrame gameFrame = new GameFrame();
        GamePanel panel = (GamePanel) gameFrame.getContentPane().getComponent(0);
        panel.gameLoop();
    }
}
