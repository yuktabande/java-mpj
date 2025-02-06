import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryDashboard extends JFrame {
    public InventoryDashboard() {
        setTitle("Inventory Dashboard");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to Inventory Management System", JLabel.CENTER);
        add(welcomeLabel);

        JButton addProductButton = new JButton("Add Products to Inventory");
        JButton placeOrderButton = new JButton("Place Purchase Order to Supplier");
        JButton viewDashboardButton = new JButton("See Dashboard");

        // Button Actions
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PlaceOrder();  // Opens PlaceOrder window
            }
        });

        add(addProductButton);
        add(placeOrderButton);
        add(viewDashboardButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        new InventoryDashboard();
    }
}
