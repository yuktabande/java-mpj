import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main frame
            JFrame frame = new JFrame("Inventory Management");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Set the layout manager for the frame
            frame.setLayout(new BorderLayout());

            // Create a panel for the form
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(4, 2));  // Grid layout for form fields

            // Add components to the panel
            panel.add(new JLabel("Product Name:"));
            JTextField productNameField = new JTextField();
            panel.add(productNameField);

            panel.add(new JLabel("Quantity:"));
            JTextField quantityField = new JTextField();
            panel.add(quantityField);

            panel.add(new JLabel("Price:"));
            JTextField priceField = new JTextField();
            panel.add(priceField);

            // Add a button to submit the form
            JButton submitButton = new JButton("Add Product");
            panel.add(submitButton);

            // ActionListener for the submit button
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String productName = productNameField.getText();
                    String quantity = quantityField.getText();
                    String price = priceField.getText();
                    
                    // Add database logic here (connect to the DB and insert the data)
                    System.out.println("Product added: " + productName + ", " + quantity + ", " + price);
                    
                    // Clear the fields
                    productNameField.setText("");
                    quantityField.setText("");
                    priceField.setText("");
                }
            });

            // Add the panel to the frame
            frame.add(panel, BorderLayout.CENTER);

            // Make the frame visible
            frame.setVisible(true);
        });
    }
}
