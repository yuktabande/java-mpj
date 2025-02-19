package com.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import javax.swing.JPanel;

public class Background extends JPanel {
    private float animate;
    private boolean showFields = true;

    public void setAnimate(float animate) {
        this.animate = animate;
        repaint();
    }

    public boolean isShowFields() {
        return showFields;
    }

    public void setShowFields(boolean showFields) {
        this.showFields = showFields;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int animHeight = (int) (height * (1 - animate));

        // Background color
        g2.setColor(new Color(220, 210, 230));
        g2.fillRect(0, animHeight, width, height);

        // Curvy Animation Effect
        g2.setColor(new Color(220, 210, 230));
        CubicCurve2D.Float curve = new CubicCurve2D.Float(0, animHeight, width / 4, animHeight - 40,
                width * 3 / 4, animHeight + 40, width, animHeight);
        g2.draw(curve);
        g2.fill(curve);
    }
}
