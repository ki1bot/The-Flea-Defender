package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BackgroundRenderer {
    public void draw(Graphics2D g, BufferedImage background, int width, int height, int idleTick) {
        if (background != null) {
            int parallaxX = (int) (Math.sin(idleTick * 0.008) * 5);
            g.drawImage(background, -16 + parallaxX, 0, width + 32, height, null);
            return;
        }

        g.setColor(new Color(72, 164, 82));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(23, 82, 48));
        g.fillRect(0, height - 80, width, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("background.png tidak ditemukan", 24, 34);
    }

    public void drawFallingLeaves(Graphics2D g, int width, int height, int idleTick) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));

        for (int i = 0; i < 14; i++) {
            int speed = 1 + i % 2;
            int x = (i * 117 + idleTick * speed) % (width + 40) - 20;
            int y = (i * 51 + idleTick * (speed + 1)) % Math.max(1, height - 48);

            if (i % 3 == 0) {
                g.setColor(new Color(92, 164, 48));
            } else if (i % 3 == 1) {
                g.setColor(new Color(126, 186, 52));
            } else {
                g.setColor(new Color(68, 128, 42));
            }

            g.fillRect(x, y, 5, 3);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }
}
