package com.dashboard;

import javax.swing.*;
import java.awt.*;
import com.swing.Theme;
import com.swing.CustomButton;

public class SidebarPanel extends JPanel {
    public SidebarPanel() {
        setLayout(new GridLayout(5, 1, 10, 10));
        setBackground(Theme.SECONDARY_COLOR);
        setPreferredSize(new Dimension(200, 0));

        JButton dashboardBtn = new CustomButton("Dashboard");
        JButton productsBtn = new CustomButton("Products");
        JButton salesBtn = new CustomButton("Sales");
        JButton reportsBtn = new CustomButton("Reports");
        JButton settingsBtn = new CustomButton("Settings");

        add(dashboardBtn);
        add(productsBtn);
        add(salesBtn);
        add(reportsBtn);
        add(settingsBtn);
    }
}
