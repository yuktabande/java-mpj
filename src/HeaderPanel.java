package com.dashboard;

import javax.swing.*;
import java.awt.*;
import com.swing.Theme;
import com.swing.CustomButton;

public class HeaderPanel extends JPanel {
    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.PRIMARY_COLOR);
        setPreferredSize(new Dimension(0, 60));

        JLabel titleLabel = new JLabel("Dashboard", JLabel.LEFT);
        titleLabel.setFont(Theme.HEADER_FONT);
        titleLabel.setForeground(Theme.TEXT_COLOR);

        JButton logoutButton = new CustomButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.addActionListener(e -> {
            new com.main.main(); // Redirect to login screen
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        add(titleLabel, BorderLayout.WEST);
        add(logoutButton, BorderLayout.EAST);
    }
}
