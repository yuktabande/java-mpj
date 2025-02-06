import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        add(loginButton);
        loginButton.addActionListener(new LoginAction());

        setVisible(true);
    }

    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(null, "Login Successful!");
                new InventoryDashboard(); // Open inventory dashboard after login
                dispose(); // Close login window
            } else {
                JOptionPane.showMessageDialog(null, "Invalid Username or Password");
            }
        }
    }

    private boolean authenticate(String username, String password) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Staff WHERE username = ? AND password_hash = ? AND is_active = TRUE")) {

            stmt.setString(1, username);
            stmt.setString(2, password); // Ideally, use hashed passwords
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // If a record is found, authentication is successful

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}
