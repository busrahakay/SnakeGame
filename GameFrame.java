//Kullanıcı arayüzü tasarlanıyor.

import javax.swing.*;
import java.awt.*;
//Java'da grafiksel kullanıcı arayüzü (GUI) oluşturmak için kullanılır.

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//GUI bileşenlerinin tıklama olaylarını dinlemek için kullanılır.

public class GameFrame extends JFrame {
    private CardLayout cardLayout; //ekranlar arası geçiş için.
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private JPanel startPanel;
    private JButton selectedButton = null; //Seçili butonun durumunu takip eder.

    public GameFrame() {   //yapıcı metot oluşturuldu.
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        gamePanel = new GamePanel();

        startPanel = new JPanel();
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS)); //paneldeki bileşenlerin dikey hizalanması.
        startPanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Snake Game");
        titleLabel.setFont(new Font("Ink Free", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Ink Free", Font.BOLD, 40));
        startButton.setForeground(Color.white);
        startButton.setBackground(Color.GREEN);
        startButton.setFocusPainted(false); //buton üzerine gelindiğinde çerçeve oluşmamasını sağlar. (odaklanma)
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //oyun başlatılması için ActionListener eklenir.
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "game");
                gamePanel.startGame();
            }
        });

        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(Color.BLACK);
        colorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JLabel colorSelectLabel = new JLabel("You can choose the background color...");
        colorSelectLabel.setFont(new Font("Ink Free", Font.BOLD, 20));
        colorSelectLabel.setForeground(Color.WHITE);
        colorSelectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        colorPanel.add(createColorButton("BLACK", Color.BLACK));
        colorPanel.add(createColorButton("PINK", Color.PINK));
        colorPanel.add(createColorButton("GRAY", Color.GRAY));
        colorPanel.add(createColorButton("ORANGE", Color.ORANGE));

        startPanel.add(Box.createRigidArea(new Dimension(250, 70)));
        startPanel.add(titleLabel);
        startPanel.add(Box.createRigidArea(new Dimension(250, 70)));
        startPanel.add(colorSelectLabel); // Renk seçim açıklaması
        startPanel.add(Box.createRigidArea(new Dimension(250, 70)));
        startPanel.add(colorPanel);
        startPanel.add(Box.createRigidArea(new Dimension(250, 70)));
        startPanel.add(startButton);

        mainPanel.add(startPanel, "start");
        mainPanel.add(gamePanel, "game");

        this.add(mainPanel);
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false); // pencerenin boyutlandırılması engellenir.
        this.pack(); //otomatik boyutlandırma.
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        cardLayout.show(mainPanel, "start");
    }

    JButton createColorButton(String name, Color color) {
        JButton button = new JButton(name);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Ink Free", Font.BOLD, 20));
        button.setFocusPainted(false);

        // Varsayılan kenarlığı ayarla
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        // Buton boyutlarını ayarla
        button.setPreferredSize(new Dimension(100, 50)); // Boyutları 200x70 piksel olarak ayarla

        button.addActionListener(e -> {
            // Önceki seçili butonu sıfırla
            if (selectedButton != null) {
                selectedButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Varsayılan kenarlık
            }

            // Bu butonu seçilmiş yap
            selectedButton = button;
            selectedButton.setBorder(BorderFactory.createLineBorder(Color.RED, 4)); // Seçili buton için vurgulu kenarlık

            // Arka plan rengini değiştir
            gamePanel.setBackgroundColor(color);
            updateStartPanelColor(color); // Başlangıç panelinin rengini değiştir
        });
        return button;
    }

    private void updateStartPanelColor(Color color) {
        startPanel.setBackground(color);
        for (Component comp : startPanel.getComponents()) {
            if (comp instanceof JPanel || comp instanceof JLabel) {
                comp.setBackground(color);
            }
        }
        startPanel.repaint();
    }

    public static void main(String[] args) { //uygulama başlatılır.
        new GameFrame();
    }
}
