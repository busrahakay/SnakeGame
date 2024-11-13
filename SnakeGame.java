import java.awt.*; //Grafiksel kullanıcı arayüzü (GUI) bileşenlerini içerir.
import java.awt.event.*; //Java'da olay dinleme (event handling) ve olay işleme için kullanılır. 
//Olaylar, kullanıcı arayüzünde kullanıcı etkileşimleri olduğunda tetiklenir (örneğin, butona tıklanması,
//klavye tuşuna basılması vb.). Bu olaylara tepki vermek için "dinleyiciler" (event listeners) eklenir.

import java.util.ArrayList; //Dinamik bir liste yapısı sağlar.
import java.util.Random; //Rastgele sayı üretimi için kullanılır.
import javax.swing.*;

//extends ile SnakeGame sınıfı JPanel sınıfı gibi davranır. (çünkü miras alındı.)
//Bir sınıf, bir veya daha fazla arayüzü (interface) uygulamak için implements 
//anahtar sözcüğünü kullanır. Bu, sınıfın belirli metotları uygulamasını zorunlu kılar.
//klavyenin hareketleri ile yılanın hareketine yön vermek için KeyListener kullanmalıyım.
//KeyListener için üç metodu geçersiz kılmam gerekir.
public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    private class Tile{ //verilen 25 birim için x ve y konumlarını takip edecek bir sınıf yarattık.
        int x;
        int y;

        Tile(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25; //jpaneli grid gibi bölümlere ayırdık ve her bir ayrılan kare 25 piksel olacak. 

    //Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody; //yılanın her bir yiyecekle büyümesi için hepsini toplayabileceğimiz 
    //bir dizi oluşturduk. 

    //Food
    Tile food;

    //Random nesnesi ile yiyeceğe rastege x ve y atayarak random yerlerde yiyecek oluşturacağız.
    Random random;

    //Game Logic
    Timer gameLoop;
    //hız için x ve y değişkenleri belirleriz.
    int velocityX;
    int velocityY;
    boolean gameOver = false; 

    SnakeGame(int boardWidth, int boardHeight){ //constracture
        this.boardHeight = boardHeight; //this anahtar kelimesi ile 
        //sınıf içerisinde ve fonksiyonda kullanılan değişkenler ayırt edilmiş oldu.
        this.boardWidth = boardWidth;
        setPreferredSize(new Dimension(this.boardHeight, this.boardWidth));
        setBackground(Color.black);
        addKeyListener(this);//oyunun tuş basışlarını dinlememizi sağlar.
        setFocusable(true);//bu ile bileşenin klavyeyle etkileşime geçebilmesine izin veriyoruz.

        snakeHead = new Tile(5, 5); //başlangıç olarak 5,5 değerleri verildi.
        //artık başlangıçta bir döşeme nesnemiz var ve bunu çizmek için paintcomponent oluşturulur.
        snakeBody = new ArrayList<Tile>(); //yılanın tüm vücut parçalarını saklayacak bir dizi listesi oluşturduk.

        food = new Tile(10, 10); //sadece bu seferlik bu şekilde ayarlandı.
        //random yerlerde yiyecek üretebilmek için Random nesnesini kullanıyoruz.
        random = new Random();
        placeFood();

        //yılanın x ve y yönündeki hızları için oluşturulmuş değişkenler.
        velocityX = 0;
        velocityY = 1;

        gameLoop = new Timer(100, this); //oyun ne kadar sürede bir tekrarlanmalı ve her 
        //tekrarlandığında ne yapması gerektiğini parantezler içine yazarark burada oyunun döngü içinde çalışmasını sağlarız.
        gameLoop.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        // //Grid = ızgara çizgileri için bir for döngüsü oluşturuldu.
        // for(int i = 0; i < boardWidth/tileSize; i++){
        //     //(x1, y1, x2, y2) her çizgi çekildiğinde bir başlangıç ve bitişe ihtiyaç duyulur.
        //     g.drawLine(i*tileSize, 0, i*tileSize, boardHeight); //dikey çizgiler çizildi.
        //     g.drawLine(0, i*tileSize, boardWidth, i*tileSize); //yatay çizgiler çizildi.
        // }

        //Food
        g.setColor(Color.red);
        //g.fillRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize); //tileSize ile 
        g.fill3DRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize, true);  

        //Snake
        g.setColor(Color.GREEN);
        //g.fillRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize); //tileSize ile 
        //çarpmamızın nedeni verilen değerleri, biriminin 25 birim olduğu piksel cinsinden yazmamız gerektiği.
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, true);

        //Snake Body
        for (int i = 0; i < snakeBody.size(); i++){
           Tile snakePart = snakeBody.get(i);
           //g.fillRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize); 
           g.fill3DRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize, true); 
        }

        //Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if(gameOver){
            g.setColor(Color.red);
            g.drawString("Game OVER: "+ String.valueOf(snakeBody.size()), tileSize-16, tileSize);
        }
        else{
            g.drawString("Score: "+ String.valueOf(snakeBody.size()), tileSize-16, tileSize);
        }
    }

    public void placeFood(){
        //bu fonksiyon yiyeceğin x ve y koordinatlarını rastgele ayarlayacak.
        food.x = random.nextInt(boardWidth/tileSize); // 600:25=24 yani verilen değer 0 ile 24 arasında olacaktır.
        food.y = random.nextInt(boardHeight/tileSize);
    }

    //yılanın başı ile yiyecek arasındaki çarpışmayı tespit edecek fonksiyon.
    public boolean collision(Tile tile1, Tile tile2){
        //x ve y konumlarını kontrol ederel aynı döşemede oldukları anları buluruz.
        //ve bu da çarpıştıklarını gösterir.
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move(){
        //yiyeceği yemek için
        if(collision(snakeHead, food)){
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        //yılanın hareketinde baş kısmı takip ettiğimizden dolayı yenen yiyecek dizinin (kendisinden bir 
        //önceki karonun kopyasını alarak) en arka kısmına eklenecektir.
        //SnakeBody
        for(int i = snakeBody.size()-1; i >= 0; i--){
            Tile snakePart = snakeBody.get(i);
            if(i == 0){ //vücut kısmının ilk üyesi snakeHead kısmını takip eder diğer 
                //gelen üyeler ise ondan önceki üyeyi takip eder.
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else{
                //kendinden öncekinin konumunu kopyalayıp alması için ilk önce kopyalama yaparız sonrasında 
                //bir sonraki üyeye atama yaparız.
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        //SnakeHead
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;
        //bu şekilde her 100 milisaniyede çağırıldığında yılanın x ve y konumu güncellenecektir.
        
        //gameOver koşulları
        //yılanın kendi vücuduyla çarpışması sonucu oyunun sonlanması için gerekli kodlar.
        for(int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            //yılanın başı ile vücudu çarpışırsa
            if(collision(snakeHead, snakePart)){
                gameOver = true;
            }
        }

        //yılanın dört duvardan birine çarparak oyunun sonlanması.
        if(snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth ||
           snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight){
            gameOver = true;
        }
    }

    //implements ActionListener yüzünden bu metot oluşturulmak zorunda kalınır.
    @Override //geçersiz kılma
    public void actionPerformed(ActionEvent e) {
        move(); //yılanın x ve y konumunu güncelleyecek bir fonksiyon.
        repaint();
        if(gameOver){
            gameLoop.stop();
        }
    }

    //yılan kendi vücuduna hareket edememesi açısından sağa 
    //gittiyse hemen arkasından sola dönememesi gerekir. && ile bu koşullar eklendi.
    //sadece klavyeye basılan tuşlarla alakalı işlem yaparak hareket edilecek.
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1){
            velocityX = 0;
            velocityY = -1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1){
            velocityX = -1;
            velocityY = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }
    }

    //kullanmayacağız ondan dolayı bir değişikliğe gerek yok.
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
