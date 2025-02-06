import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PlaceOrder extends JFrame {
    private JTextField productIdField, quantityField, supplierIdField, staffIdField;
    private JButton placeOrderButton;

    public PlaceOrder() {
        setTitle("Place Purchase Order");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Labels and Fields
        add(new JLabel("Product ID:"));
        productIdField = new JTextField();
        add(productIdField);

        add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        add(quantityField);

        add(new JLabel("Supplier ID:"));
        supplierIdField = new JTextField();
        add(supplierIdField);

        add(new JLabel("Staff ID:"));
        staffIdField = new JTextField();
        add(staffIdField);

        placeOrderButton = new JButton("Place Order");
        add(placeOrderButton);

        // Button Action
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });

        setVisible(true);
    }

    private void placeOrder() {
        String productId = productIdField.getText();
        String quantity = quantityField.getText();
        String supplierId = supplierIdField.getText();
        String staffId = staffIdField.getText();

        if (productId.isEmpty() || quantity.isEmpty() || supplierId.isEmpty() || staffId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            conn.setAutoCommit(false); // Start transaction

            // 🔹 Step 1: Insert into PurchaseOrders
            String orderQuery = "INSERT INTO PurchaseOrders (supplier_id, staff_id, order_date, total_amount) VALUES (?, ?, NOW(), 0.00)";
            PreparedStatement psOrder = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            psOrder.setInt(1, Integer.parseInt(supplierId));
            psOrder.setInt(2, Integer.parseInt(staffId));

            int affectedRows = psOrder.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating purchase order failed, no rows affected.");
            }

            // 🔹 Step 2: Retrieve generated po_id
            ResultSet rs = psOrder.getGeneratedKeys();
            int poId = -1;
            if (rs.next()) {
                poId = rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve purchase order ID.");
            }

            // 🔹 Step 3: Insert into PurchaseOrderItems
            String itemQuery = "INSERT INTO PurchaseOrderItems (po_id, product_id, quantity, unit_cost) VALUES (?, ?, ?, ?)";
            PreparedStatement psItem = conn.prepareStatement(itemQuery);
            psItem.setInt(1, poId);
            psItem.setInt(2, Integer.parseInt(productId));
            psItem.setInt(3, Integer.parseInt(quantity));
            psItem.setBigDecimal(4, new java.math.BigDecimal("10.00")); // Example unit cost

            int itemRows = psItem.executeUpdate();
            if (itemRows == 0) {
                throw new SQLException("Adding purchase order item failed.");
            }

            conn.commit(); // Commit transaction

            JOptionPane.showMessageDialog(this, "Order Placed Successfully!");
            psOrder.close();
            psItem.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new PlaceOrder();
    }
}
