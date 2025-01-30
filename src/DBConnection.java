import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        try {
            System.out.println("Attempting to connect...");

            // Explicitly load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/ShopInventory";
            String user = "root";  
            String password = "manju2606";  

            Connection conn = DriverManager.getConnection(url, user, password);

            if (conn != null) {
                System.out.println("Connection Successful!");
            } else {
                System.out.println("Connection Failed!");
            }

            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting database connection test...");
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Database Connected Successfully!");
        } else {
            System.out.println("Database Connection Failed!");
        }
    }
}
