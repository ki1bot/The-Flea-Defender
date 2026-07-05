package gui;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class BattlePanel extends JPanel {
    private final Deque<String> animationQueue;
    private final Timer animationTimer;

    private BufferedImage defenderSheet;
    private BufferedImage fleaSheet;
    private BufferedImage forestBackground;

    private String currentAnimation;
    private int frame;
    private int idleTick;

    public BattlePanel() {
        animationQueue = new ArrayDeque<>();
        currentAnimation = "idle";
        frame = 0;
        idleTick = 0;

        setPreferredSize(new Dimension(720, 260));
        setMinimumSize(new Dimension(620, 230));
        setBackground(Color.BLACK);

        loadAssets();

        animationTimer = new Timer(33, event -> updateAnimation());
        animationTimer.start();
    }

    public void playDefenderAttack() {
        enqueueAnimation("defender_attack");
    }

    public void playDefenderDefend() {
        enqueueAnimation("defender_defend");
    }

    public void playFleaAttack() {
        enqueueAnimation("flea_attack");
    }

    public void playFleaDefend() {
        enqueueAnimation("flea_defend");
    }

    public void playHeal() {
        enqueueAnimation("heal");
    }

    public void clearAnimations() {
        animationQueue.clear();
        currentAnimation = "idle";
        frame = 0;
        repaint();
    }

    private void loadAssets() {
        defenderSheet = makeBackgroundTransparent(loadImage("src/assets/defender.png", "/assets/defender.png"));
        fleaSheet = makeBackgroundTransparent(loadImage("src/assets/flea.png", "/assets/flea.png"));
        forestBackground = loadImage("src/assets/background.png", "/assets/background.png");
    }

    private BufferedImage loadImage(String filePath, String resourcePath) {
        try {
            File file = new File(filePath);

            if (file.exists()) {
                return ImageIO.read(file);
            }

            InputStream inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream != null) {
                return ImageIO.read(inputStream);
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private BufferedImage makeBackgroundTransparent(BufferedImage source) {
        if (source == null) {
            return null;
        }

        BufferedImage result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int pixel = source.getRGB(x, y);

                int alpha = (pixel >> 24) & 255;
                int red = (pixel >> 16) & 255;
                int green = (pixel >> 8) & 255;
                int blue = pixel & 255;

                boolean lightBackground = red > 225 && green > 225 && blue > 225;
                boolean neutralBackground = Math.abs(red - green) < 12 && Math.abs(green - blue) < 12 && red > 215;

                if (lightBackground || neutralBackground) {
                    result.setRGB(x, y, 0);
                } else {
                    result.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
                }
            }
        }

        return result;
    }

    private void enqueueAnimation(String animation) {
        animationQueue.add(animation);

        if ("idle".equals(currentAnimation)) {
            startNextAnimation();
        }
    }

    private void updateAnimation() {
        idleTick++;

        if ("idle".equals(currentAnimation)) {
            if (!animationQueue.isEmpty()) {
                startNextAnimation();
            }

            repaint();
            return;
        }

        frame++;

        if (frame >= getAnimationDuration(currentAnimation)) {
            currentAnimation = "idle";
            frame = 0;

            if (!animationQueue.isEmpty()) {
                startNextAnimation();
            }
        }

        repaint();
    }

    private void startNextAnimation() {
        String next = animationQueue.poll();

        if (next != null) {
            currentAnimation = next;
            frame = 0;
        }
    }

    private int getAnimationDuration(String animation) {
        if ("defender_attack".equals(animation) || "flea_attack".equals(animation)) {
            return 34;
        }

        if ("defender_defend".equals(animation) || "flea_defend".equals(animation)) {
            return 38;
        }

        if ("heal".equals(animation)) {
            return 42;
        }

        return 1;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int width = getWidth();
        int height = getHeight();

        drawForestBackground(g, width, height);

        int groundY = height - 52;
        int defenderBaseX = Math.max(95, width / 4 - 35);
        int fleaBaseX = Math.min(width - 190, width * 3 / 4 - 55);

        int defenderOffset = getDefenderOffset();
        int fleaOffset = getFleaOffset();

        int defenderBob = (int) (Math.sin(idleTick * 0.18) * 2);
        int fleaBob = (int) (Math.cos(idleTick * 0.20) * 2);

        drawShadow(g, defenderBaseX + defenderOffset + 72, groundY + 4, 84, 14);
        drawShadow(g, fleaBaseX + fleaOffset + 78, groundY + 6, 110, 16);

        drawDefenderSprite(g, defenderBaseX + defenderOffset, groundY - 145 + defenderBob);
        drawFleaSprite(g, fleaBaseX + fleaOffset, groundY - 128 + fleaBob);

        drawEffects(g, defenderBaseX + defenderOffset + 76, fleaBaseX + fleaOffset + 88, groundY);
        drawCaption(g, width);

        g.dispose();
    }

    private void drawForestBackground(Graphics2D g, int width, int height) {
        if (forestBackground != null) {
            g.drawImage(forestBackground, 0, 0, width, height, null);
            return;
        }

        g.setColor(new Color(72, 164, 82));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(23, 82, 48));
        g.fillRect(0, height - 80, width, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("forest_background.png tidak ditemukan", 24, 34);
    }

    private void drawDefenderSprite(Graphics2D g, int x, int y) {
        if (defenderSheet == null) {
            drawMissingSprite(g, x, y, "DEFENDER");
            return;
        }

        int spriteFrame = getDefenderSpriteFrame();
        drawSpriteFrame(g, defenderSheet, spriteFrame, x, y, 165, 145);
    }

    private void drawFleaSprite(Graphics2D g, int x, int y) {
        if (fleaSheet == null) {
            drawMissingSprite(g, x, y, "FLEA");
            return;
        }

        int spriteFrame = getFleaSpriteFrame();
        drawSpriteFrame(g, fleaSheet, spriteFrame, x, y, 180, 130);
    }

    private void drawSpriteFrame(Graphics2D g, BufferedImage sheet, int frameIndex, int x, int y, int drawWidth, int drawHeight) {
        int totalFrames = 4;
        int frameWidth = sheet.getWidth() / totalFrames;
        int frameHeight = sheet.getHeight();

        int sourceX1 = frameIndex * frameWidth;
        int sourceY1 = 0;
        int sourceX2 = sourceX1 + frameWidth;
        int sourceY2 = frameHeight;

        g.drawImage(
                sheet,
                x,
                y,
                x + drawWidth,
                y + drawHeight,
                sourceX1,
                sourceY1,
                sourceX2,
                sourceY2,
                null
        );
    }

    private int getDefenderSpriteFrame() {
        if ("defender_attack".equals(currentAnimation)) {
            if (frame < 10) {
                return 1;
            }

            if (frame < 25) {
                return 2;
            }

            return 0;
        }

        if ("defender_defend".equals(currentAnimation)) {
            return 3;
        }

        if ("heal".equals(currentAnimation)) {
            return 0;
        }

        return idleTick / 18 % 2 == 0 ? 0 : 1;
    }

    private int getFleaSpriteFrame() {
        if ("flea_attack".equals(currentAnimation)) {
            if (frame < 10) {
                return 1;
            }

            if (frame < 26) {
                return 2;
            }

            return 0;
        }

        if ("flea_defend".equals(currentAnimation)) {
            return 3;
        }

        return idleTick / 20 % 2 == 0 ? 0 : 1;
    }

    private void drawMissingSprite(Graphics2D g, int x, int y, String text) {
        g.setColor(new Color(180, 30, 30));
        g.fillRect(x, y, 120, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString(text, x + 18, y + 45);
    }

    private void drawShadow(Graphics2D g, int x, int y, int width, int height) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g.setColor(Color.BLACK);
        g.fillOval(x - width / 2, y - height / 2, width, height);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawEffects(Graphics2D g, int defenderX, int fleaX, int groundY) {
        if ("defender_attack".equals(currentAnimation)) {
            drawSlash(g, fleaX - 20, groundY - 102, false);
            drawFloatingText(g, "HIT!", fleaX - 28, groundY - 128, new Color(255, 230, 90));
        }

        if ("flea_attack".equals(currentAnimation)) {
            drawSlash(g, defenderX + 42, groundY - 112, true);
            drawFloatingText(g, "DAMAGE!", defenderX - 36, groundY - 136, new Color(255, 86, 72));
        }

        if ("defender_defend".equals(currentAnimation)) {
            drawShieldAura(g, defenderX + 4, groundY - 82, new Color(55, 125, 230));
            drawFloatingText(g, "DEFEND", defenderX - 34, groundY - 138, new Color(100, 180, 255));
        }

        if ("flea_defend".equals(currentAnimation)) {
            drawShieldAura(g, fleaX, groundY - 76, new Color(175, 72, 180));
        }

        if ("heal".equals(currentAnimation)) {
            drawHealEffect(g, defenderX, groundY - 96);
            drawFloatingText(g, "HEAL", defenderX - 20, groundY - 138, new Color(80, 230, 110));
        }
    }

    private void drawSlash(Graphics2D g, int x, int y, boolean reverse) {
        if (frame < 8 || frame > 24) {
            return;
        }

        float alpha = Math.max(0f, 1f - Math.abs(16 - frame) / 12f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 238, 120));

        if (reverse) {
            g.drawLine(x + 58, y, x + 8, y + 52);
            g.drawLine(x + 68, y + 14, x + 18, y + 64);
        } else {
            g.drawLine(x, y, x + 58, y + 52);
            g.drawLine(x - 10, y + 14, x + 48, y + 64);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawShieldAura(Graphics2D g, int x, int y, Color color) {
        float alpha = 0.25f + (float) Math.abs(Math.sin(frame * 0.24)) * 0.35f;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);
        g.setStroke(new BasicStroke(5f));
        g.drawOval(x - 58, y - 58, 116, 116);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x - 70, y - 70, 140, 140);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawHealEffect(Graphics2D g, int x, int y) {
        float alpha = Math.max(0.15f, 1f - frame / 42f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(new Color(75, 235, 108));

        for (int i = 0; i < 5; i++) {
            int offsetX = (i - 2) * 22;
            int offsetY = -frame - (i % 2) * 12;
            drawPlus(g, x + offsetX, y + offsetY, 10);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawPlus(Graphics2D g, int x, int y, int size) {
        g.fillRect(x - size / 2, y - size * 2, size, size * 4);
        g.fillRect(x - size * 2, y - size / 2, size * 4, size);
    }

    private void drawFloatingText(Graphics2D g, String text, int x, int y, Color color) {
        float alpha = Math.max(0f, 1f - frame / 34f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setFont(new Font("Arial", Font.BOLD, 22));

        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, y - frame + 2);

        g.setColor(color);
        g.drawString(text, x, y - frame);

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawCaption(Graphics2D g, int width) {
        String caption = getCaption();

        g.setFont(new Font("Arial", Font.BOLD, 14));

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(caption);

        g.setColor(Color.BLACK);
        g.drawString(caption, (width - textWidth) / 2 + 2, 26);

        g.setColor(Color.WHITE);
        g.drawString(caption, (width - textWidth) / 2, 24);
    }

    private String getCaption() {
        if ("defender_attack".equals(currentAnimation)) {
            return "Defender menyerang Flea";
        }

        if ("flea_attack".equals(currentAnimation)) {
            return "Flea menyerang Defender";
        }

        if ("defender_defend".equals(currentAnimation)) {
            return "Defender bertahan";
        }

        if ("flea_defend".equals(currentAnimation)) {
            return "Flea terkena serangan";
        }

        if ("heal".equals(currentAnimation)) {
            return "Defender memulihkan HP";
        }

        return "Pertarungan di Hutan";
    }

    private int getDefenderOffset() {
        if (!"defender_attack".equals(currentAnimation)) {
            return 0;
        }

        if (frame <= 12) {
            return frame * 7;
        }

        if (frame <= 23) {
            return 84;
        }

        return Math.max(0, 84 - (frame - 23) * 8);
    }

    private int getFleaOffset() {
        if (!"flea_attack".equals(currentAnimation)) {
            return 0;
        }

        if (frame <= 12) {
            return -frame * 7;
        }

        if (frame <= 23) {
            return -84;
        }

        return Math.min(0, -84 + (frame - 23) * 8);
    }
}
