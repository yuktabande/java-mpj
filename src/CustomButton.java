package com.swing;

import javax.swing.*;
import java.awt.*;

public class CustomButton extends JButton {
    public CustomButton(String text) {
        super(text);
        setFont(Theme.BUTTON_FONT);
        setForeground(Color.WHITE);
        setBackground(Theme.SECONDARY_COLOR);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
