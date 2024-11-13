import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GamePanel gamePanel;

    public GameFrame() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        gamePanel = new GamePanel();

        JPanel startPanel = new JPanel();
        startPanel.setBackground(Color.BLACK);
        JButton startButton = new JButton("Başla");
        startButton.setFont(new Font("Ink Free", Font.BOLD, 40));
        startButton.setForeground(Color.white);
        startButton.setBackground(Color.yellow);
        startButton.setFocusPainted(false);
        startPanel.add(startButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "game");
                gamePanel.startGame();
            }
        });

        mainPanel.add(startPanel, "start");
        mainPanel.add(gamePanel, "game");

        this.add(mainPanel);
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        cardLayout.show(mainPanel, "start");
    }
}