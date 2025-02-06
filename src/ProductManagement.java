import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ProductManagement extends JFrame {
    private JTextField nameField, categoryField, priceField, stockField, supplierField;
    private JButton addButton, updateButton, deleteButton, viewButton;

    public ProductManagement() {
        setTitle("Product Management");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Product Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Category ID:"));
        categoryField = new JTextField();
        add(categoryField);

        add(new JLabel("Price:"));
        priceField = new JTextField();
        add(priceField);

        add(new JLabel("Stock:"));
        stockField = new JTextField();
        add(stockField);

        add(new JLabel("Supplier ID:"));
        supplierField = new JTextField();
        add(supplierField);

        addButton = new JButton("Add Product");
        updateButton = new JButton("Update Product");
        deleteButton = new JButton("Delete Product");
        viewButton = new JButton("View Products");

        add(addButton);
        add(updateButton);
        add(deleteButton);
        add(viewButton);

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        viewButton.addActionListener(e -> viewProducts());

        setVisible(true);
    }

    private void addProduct() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Product (product_name, category_id, price, stock_quantity, supplier_id) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, nameField.getText());
            stmt.setInt(2, Integer.parseInt(categoryField.getText()));
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setInt(4, Integer.parseInt(stockField.getText()));
            stmt.setInt(5, Integer.parseInt(supplierField.getText()));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product Added Successfully!");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateProduct() {
        String productId = JOptionPane.showInputDialog("Enter Product ID to Update:");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Product SET product_name=?, category_id=?, price=?, stock_quantity=?, supplier_id=? WHERE product_id=?")) {
            stmt.setString(1, nameField.getText());
            stmt.setInt(2, Integer.parseInt(categoryField.getText()));
            stmt.setDouble(3, Double.parseDouble(priceField.getText()));
            stmt.setInt(4, Integer.parseInt(stockField.getText()));
            stmt.setInt(5, Integer.parseInt(supplierField.getText()));
            stmt.setInt(6, Integer.parseInt(productId));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product Updated Successfully!");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteProduct() {
        String productId = JOptionPane.showInputDialog("Enter Product ID to Delete:");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Product WHERE product_id=?")) {
            stmt.setInt(1, Integer.parseInt(productId));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Product Deleted Successfully!");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewProducts() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Product");
             ResultSet rs = stmt.executeQuery()) {

            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append("ID: ").append(rs.getInt("product_id"))
                        .append(", Name: ").append(rs.getString("product_name"))
                        .append(", Price: ").append(rs.getDouble("price"))
                        .append(", Stock: ").append(rs.getInt("stock_quantity"))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(null, result.toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ProductManagement();
    }
}
