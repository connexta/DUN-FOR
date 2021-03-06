package org.codice.imaging.simple.viewer;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

public class JDesktopImage implements Border {
    private final BufferedImage image;

    public JDesktopImage(BufferedImage image) {
        this.image = image;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (image != null) {
            int x0 = x + (width - image.getWidth()) / 2;
            int y0 = y + (height - image.getHeight()) / 2;
            g.drawImage(image, x0, y0, null);
        }
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
        return true;
    }
}
