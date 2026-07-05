package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class SpriteSheet {
    private final String name;
    private final BufferedImage image;
    private final int totalFrames;
    private final Rectangle[] opaqueBounds;
    private int maxOpaqueHeight;

    public SpriteSheet(String name, BufferedImage image, int totalFrames) {
        this.name = name;
        this.image = image;
        this.totalFrames = totalFrames;
        this.opaqueBounds = new Rectangle[totalFrames];
        this.maxOpaqueHeight = 1;
        cacheOpaqueBounds();
    }

    public Rectangle calculateDestination(int frameIndex, int centerX, int groundY, int targetHeight) {
        if (image == null) {
            return new Rectangle(centerX - 40, groundY - targetHeight, 80, targetHeight);
        }

        Rectangle sourceBounds = getOpaqueBounds(frameIndex);

        if (sourceBounds.width <= 0 || sourceBounds.height <= 0) {
            return new Rectangle(centerX - 40, groundY - targetHeight, 80, targetHeight);
        }

        double scale = targetHeight / (double) maxOpaqueHeight;
        int targetWidth = Math.max(1, (int) Math.round(sourceBounds.width * scale));
        int scaledHeight = Math.max(1, (int) Math.round(sourceBounds.height * scale));

        int x = centerX - targetWidth / 2;
        int y = groundY - scaledHeight;

        return new Rectangle(x, y, targetWidth, scaledHeight);
    }

    public void drawGrounded(Graphics2D g, int frameIndex, int centerX, int groundY, int targetHeight, float alpha) {
        if (alpha <= 0.02f) {
            return;
        }

        if (image == null) {
            drawMissingSprite(g, centerX - 45, groundY - targetHeight);
            return;
        }

        Rectangle sourceBounds = getOpaqueBounds(frameIndex);

        if (sourceBounds.width <= 0 || sourceBounds.height <= 0) {
            drawMissingSprite(g, centerX - 45, groundY - targetHeight);
            return;
        }

        Rectangle destination = calculateDestination(frameIndex, centerX, groundY, targetHeight);

        int frameWidth = image.getWidth() / totalFrames;
        int sourceX1 = frameIndex * frameWidth + sourceBounds.x;
        int sourceY1 = sourceBounds.y;
        int sourceX2 = sourceX1 + sourceBounds.width;
        int sourceY2 = sourceY1 + sourceBounds.height;

        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g.drawImage(
                image,
                destination.x,
                destination.y,
                destination.x + destination.width,
                destination.y + destination.height,
                sourceX1,
                sourceY1,
                sourceX2,
                sourceY2,
                null
        );

        g.setComposite(oldComposite);
    }

    private Rectangle getOpaqueBounds(int frameIndex) {
        int index = Math.max(0, Math.min(totalFrames - 1, frameIndex));
        return opaqueBounds[index];
    }

    private void cacheOpaqueBounds() {
        for (int i = 0; i < totalFrames; i++) {
            opaqueBounds[i] = calculateOpaqueBounds(i);

            if (opaqueBounds[i].height > maxOpaqueHeight) {
                maxOpaqueHeight = opaqueBounds[i].height;
            }
        }

        if (maxOpaqueHeight <= 0) {
            maxOpaqueHeight = 1;
        }
    }

    private Rectangle calculateOpaqueBounds(int frameIndex) {
        if (image == null) {
            return new Rectangle(0, 0, 0, 0);
        }

        int frameWidth = image.getWidth() / totalFrames;
        int frameHeight = image.getHeight();
        int startX = frameIndex * frameWidth;

        int minX = frameWidth;
        int minY = frameHeight;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < frameHeight; y++) {
            for (int x = 0; x < frameWidth; x++) {
                int alpha = (image.getRGB(startX + x, y) >> 24) & 255;

                if (alpha > 20) {
                    if (x < minX) {
                        minX = x;
                    }

                    if (y < minY) {
                        minY = y;
                    }

                    if (x > maxX) {
                        maxX = x;
                    }

                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return new Rectangle(0, 0, 0, 0);
        }

        return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private void drawMissingSprite(Graphics2D g, int x, int y) {
        g.setColor(new Color(180, 30, 30));
        g.fillRect(x, y, 90, 70);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(name, x + 16, y + 40);
    }
}
