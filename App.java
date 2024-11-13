import javax.swing.*; //kullanıcı arayüzleri oluşturmaya olanak tanır.
//JFrame, JButton, JLabel gibi bileşenlerin tanımlandığı pakettir.

public class App{
    public static void main(String[] args) throws Exception{
        int boardWidth = 600;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("snakeGame");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        
        frame.setLocationRelativeTo(null); //pencerenin ekranın ortasında görüntülenmesini sağlar. null parametresi, 
        //pencerenin belirli bir bileşene göre konumlandırılmamasını, bunun yerine ekranın tam ortasına yerleştirilmesini ifade eder.
        
        frame.setResizable(false); //pencerenin boyutunun kullanıcının değiştirebilmesini (yeniden 
        //boyutlandırmasını) engeller. false değeri, pencerenin sabit boyutlu olmasını sağlar.
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //pencere kapatıldığında uygulamanın sonlanmasını sağlar. 


        SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
        frame.add(snakeGame); //bu şekilde bırakılırsa panelde başlık yazısı yüzünden
        //panel tam olarak 600x600 piksel şeklinde boyutlanmıyor.
        frame.pack(); //bu komutla beraber jpanel tam boyutlarına ulaşabiliyor.
        
        snakeGame.requestFocus(); //yılan oyunumuz tuşa basmayı dinleyen bileşen olacaktır.
    }
}