import javax.swing.*;
import java.awt.*;

public class InventoryDashboard extends JFrame {
    public InventoryDashboard() {
        setTitle("Inventory Dashboard");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to Inventory Management System", JLabel.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new InventoryDashboard();
    }
}
