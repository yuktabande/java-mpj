package com.dashboard;

import javax.swing.*;
import java.awt.*;
import com.swing.Theme;

public class ContentPanel extends JPanel {
    public ContentPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Welcome to Dashboard!", JLabel.CENTER);
        welcomeLabel.setFont(Theme.HEADER_FONT);
        welcomeLabel.setForeground(Theme.TEXT_COLOR);
        
        add(welcomeLabel, BorderLayout.CENTER);
    }
}
