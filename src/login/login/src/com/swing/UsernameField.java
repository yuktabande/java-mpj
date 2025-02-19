package com.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class UsernameField extends JTextField {
    private String labelText = "Username";
    private boolean showLabel = false;
    private final Animator animator;
    private float labelY = 20;

    public UsernameField() {
        setFont(new Font("SansSerif", Font.PLAIN, 16));
        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                labelY = 20 - (fraction * 10);
                repaint();
            }
        };

        animator = new Animator(200, target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                showLabel = true;
                animator.start();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    showLabel = false;
                    animator.start();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(Color.GRAY);

        if (!showLabel && getText().isEmpty()) {
            g2.drawString(labelText, 10, 24);
        } else {
            g2.drawString(labelText, 10, (int) labelY);
        }
    }
}
