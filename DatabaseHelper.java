import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    // Veritabanı bağlantı URL'si
    private static final String DB_PATH = "C:\\Users\\CASPER\\Desktop\\developed\\SnakeGame.accdb";
    private static final String DB_URL = "jdbc:ucanaccess://" + DB_PATH;
    //UCanAccess sürücüsü kullanılarak Access veri tabanına erişmek amaçlanır.

    // Skorları veritabanına kaydet
    public static void saveScore(int greenScore, int blueScore) {
        String insertQuery = "INSERT INTO Scores (GreenSnakeScore, BlueSnakeScore) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            // Parametreler atanır.
            stmt.setInt(1, greenScore);
            stmt.setInt(2, blueScore);

            // Sorgu çalıştırılır
            stmt.executeUpdate();
            System.out.println("Skor başarıyla kaydedildi!");

        } catch (SQLException e) {
            System.out.println("Veri kaydetme hatası: " + e.getMessage());
        }
    }

    // Son üç skoru getir
    public static List<String> getLastThreeScores() {
        String selectQuery = "SELECT TOP 3 GreenSnakeScore, BlueSnakeScore FROM Scores ORDER BY ID DESC"; //azalan sıralama yapılarak son üç kayıt alınır
        List<String> scores = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
                Statement stmt = conn.createStatement(); //sql sorgularını çalıştırmak için kullanılır
                ResultSet rs = stmt.executeQuery(selectQuery)) {

            while (rs.next()) {
                int greenScore = rs.getInt("GreenSnakeScore");
                int blueScore = rs.getInt("BlueSnakeScore");
                scores.add("Green: " + greenScore + " - Blue: " + blueScore);
            }

        } catch (SQLException e) {
            System.out.println("Veri çekme hatası: " + e.getMessage());
        }

        return scores;
    }
}