package com.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import com.swing.Background;
import com.swing.UsernameField;
import com.swing.PasswordField;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

public class main extends JFrame {
    private Background background;
    private UsernameField usernameField;
    private PasswordField passwordField;
    private JButton loginButton;
    private final Animator animator;
    private JPanel titleBar;
    private JButton closeButton, minimizeButton;
    private Point initialClick;

    public main() {
        setUndecorated(true);
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        background = new Background();
        background.setLayout(null);
        setContentPane(background);

        addTitleBar();

        usernameField = new UsernameField();
        usernameField.setToolTipText("Enter Username");
        usernameField.setBounds(50, 150, 300, 40);
        background.add(usernameField);

        passwordField = new PasswordField();
        passwordField.setToolTipText("Enter Password");
        passwordField.setBounds(50, 200, 300, 40);
        background.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setBackground(new Color(3, 155, 216));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(50, 260, 300, 40);
        background.add(loginButton);

        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                background.setAnimate(fraction);
                if (fraction > 0.5) {
                    background.setShowFields(false);
                    usernameField.setVisible(false);
                    passwordField.setVisible(false);
                    loginButton.setVisible(false);
                }
                if (fraction == 1) {
                    background.setShowFields(true);
                    usernameField.setVisible(true);
                    passwordField.setVisible(true);
                    loginButton.setVisible(true);
                }
            }
        };
        animator = new Animator(800, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animator.start();
            }
        });
    }

    private void addTitleBar() {
        titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBackground(new Color(50, 50, 50));
        titleBar.setBounds(0, 0, getWidth(), 30);
        background.add(titleBar);

        closeButton = new JButton("X");
        closeButton.setBounds(getWidth() - 40, 0, 40, 30);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(200, 50, 50));
        closeButton.setBorder(null);
        closeButton.setFocusable(false);
        closeButton.addActionListener(e -> System.exit(0));
        titleBar.add(closeButton);

        minimizeButton = new JButton("-");
        minimizeButton.setBounds(getWidth() - 80, 0, 40, 30);
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setBackground(new Color(100, 100, 100));
        minimizeButton.setBorder(null);
        minimizeButton.setFocusable(false);
        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        titleBar.add(minimizeButton);

        // Enable dragging the window
        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new main().setVisible(true));
    }
}
