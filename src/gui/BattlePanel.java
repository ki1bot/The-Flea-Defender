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
import java.awt.Rectangle;
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
    private boolean fleaVisible;

    public BattlePanel() {
        animationQueue = new ArrayDeque<>();
        currentAnimation = "idle";
        frame = 0;
        idleTick = 0;
        fleaVisible = false;

        setPreferredSize(new Dimension(900, 300));
        setMinimumSize(new Dimension(760, 260));
        setBackground(Color.BLACK);

        loadAssets();

        animationTimer = new Timer(28, event -> updateAnimation());
        animationTimer.start();
    }

    public void setFleaVisible(boolean fleaVisible) {
        this.fleaVisible = fleaVisible;
        repaint();
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

                boolean almostWhite = red > 232 && green > 232 && blue > 232;
                boolean lightGray = red > 212 && green > 212 && blue > 212 && Math.abs(red - green) < 18 && Math.abs(green - blue) < 18;

                if (alpha == 0 || almostWhite || lightGray) {
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
            return 28;
        }

        if ("defender_defend".equals(animation) || "flea_defend".equals(animation)) {
            return 24;
        }

        if ("heal".equals(animation)) {
            return 32;
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
        drawFallingLeaves(g, width, height);

        int groundY = (int) (height * 0.735);
        int defenderBaseX = width / 4;
        int fleaBaseX = width * 3 / 4;

        int defenderX = defenderBaseX + getDefenderOffset();
        int fleaX = fleaBaseX + getFleaOffset();

        int shakeX = getScreenShakeX();

        g.translate(shakeX, 0);

        int defenderFrame = getDefenderSpriteFrame();
        int fleaFrame = getFleaSpriteFrame();

        Rectangle defenderBounds = calculateSpriteDestination(defenderSheet, defenderFrame, defenderX, groundY, 118);
        Rectangle fleaBounds = calculateSpriteDestination(fleaSheet, fleaFrame, fleaX, groundY, 94);

        drawShadow(g, defenderX, groundY + 4, Math.max(58, defenderBounds.width - 26), 12);

        if (fleaVisible) {
            drawShadow(g, fleaX, groundY + 4, Math.max(76, fleaBounds.width - 22), 13);
        }

        drawMotionTrail(g, defenderFrame, fleaFrame, defenderX, fleaX, groundY);

        drawSpriteGrounded(g, defenderSheet, defenderFrame, defenderX, groundY, 118);

        if (fleaVisible) {
            drawSpriteGrounded(g, fleaSheet, fleaFrame, fleaX, groundY, 94);
        }

        drawDust(g, defenderX, fleaX, groundY);

        if (fleaVisible || "defender_attack".equals(currentAnimation)) {
            drawEffects(g, defenderBounds, fleaBounds);
        } else if ("heal".equals(currentAnimation) || "defender_defend".equals(currentAnimation)) {
            drawEffects(g, defenderBounds, fleaBounds);
        }

        g.translate(-shakeX, 0);

        drawCaption(g, width);

        g.dispose();
    }

    private void drawForestBackground(Graphics2D g, int width, int height) {
        if (forestBackground != null) {
            int parallaxX = (int) (Math.sin(idleTick * 0.008) * 5);
            g.drawImage(forestBackground, -16 + parallaxX, 0, width + 32, height, null);
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

    private void drawFallingLeaves(Graphics2D g, int width, int height) {
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

    private Rectangle calculateSpriteDestination(BufferedImage sheet, int frameIndex, int centerX, int groundY, int targetHeight) {
        if (sheet == null) {
            return new Rectangle(centerX - 40, groundY - targetHeight, 80, targetHeight);
        }

        Rectangle sourceBounds = getOpaqueBounds(sheet, frameIndex);

        if (sourceBounds.width <= 0 || sourceBounds.height <= 0) {
            return new Rectangle(centerX - 40, groundY - targetHeight, 80, targetHeight);
        }

        int targetWidth = Math.max(1, (int) (targetHeight * (sourceBounds.width / (double) sourceBounds.height)));
        int x = centerX - targetWidth / 2;
        int y = groundY - targetHeight;

        return new Rectangle(x, y, targetWidth, targetHeight);
    }

    private void drawSpriteGrounded(Graphics2D g, BufferedImage sheet, int frameIndex, int centerX, int groundY, int targetHeight) {
        if (sheet == null) {
            drawMissingSprite(g, centerX - 45, groundY - targetHeight, "SPRITE");
            return;
        }

        Rectangle sourceBounds = getOpaqueBounds(sheet, frameIndex);

        if (sourceBounds.width <= 0 || sourceBounds.height <= 0) {
            drawMissingSprite(g, centerX - 45, groundY - targetHeight, "SPRITE");
            return;
        }

        Rectangle destination = calculateSpriteDestination(sheet, frameIndex, centerX, groundY, targetHeight);

        int totalFrames = 4;
        int frameWidth = sheet.getWidth() / totalFrames;
        int sourceX1 = frameIndex * frameWidth + sourceBounds.x;
        int sourceY1 = sourceBounds.y;
        int sourceX2 = sourceX1 + sourceBounds.width;
        int sourceY2 = sourceY1 + sourceBounds.height;

        g.drawImage(
                sheet,
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
    }

    private Rectangle getOpaqueBounds(BufferedImage sheet, int frameIndex) {
        int totalFrames = 4;
        int frameWidth = sheet.getWidth() / totalFrames;
        int frameHeight = sheet.getHeight();
        int startX = frameIndex * frameWidth;

        int minX = frameWidth;
        int minY = frameHeight;
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < frameHeight; y++) {
            for (int x = 0; x < frameWidth; x++) {
                int alpha = (sheet.getRGB(startX + x, y) >> 24) & 255;

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

    private int getDefenderSpriteFrame() {
        if ("defender_attack".equals(currentAnimation)) {
            if (frame < 8) {
                return 1;
            }

            if (frame < 20) {
                return 2;
            }

            return 0;
        }

        if ("defender_defend".equals(currentAnimation)) {
            return 3;
        }

        if ("heal".equals(currentAnimation)) {
            return frame / 8 % 2 == 0 ? 0 : 3;
        }

        return idleTick / 22 % 2 == 0 ? 0 : 1;
    }

    private int getFleaSpriteFrame() {
        if ("flea_attack".equals(currentAnimation)) {
            if (frame < 8) {
                return 1;
            }

            if (frame < 20) {
                return 2;
            }

            return 0;
        }

        if ("flea_defend".equals(currentAnimation)) {
            return 3;
        }

        return idleTick / 24 % 2 == 0 ? 0 : 1;
    }

    private void drawMissingSprite(Graphics2D g, int x, int y, String text) {
        g.setColor(new Color(180, 30, 30));
        g.fillRect(x, y, 90, 70);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString(text, x + 16, y + 40);
    }

    private void drawShadow(Graphics2D g, int centerX, int centerY, int width, int height) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g.setColor(Color.BLACK);
        g.fillOval(centerX - width / 2, centerY - height / 2, width, height);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawMotionTrail(Graphics2D g, int defenderFrame, int fleaFrame, int defenderX, int fleaX, int groundY) {
        if ("defender_attack".equals(currentAnimation) && frame >= 8 && frame <= 20 && defenderSheet != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
            drawSpriteGrounded(g, defenderSheet, defenderFrame, defenderX - 22, groundY, 118);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            drawSpriteGrounded(g, defenderSheet, defenderFrame, defenderX - 42, groundY, 118);

            g.setComposite(AlphaComposite.SrcOver);
        }

        if (fleaVisible && "flea_attack".equals(currentAnimation) && frame >= 8 && frame <= 20 && fleaSheet != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
            drawSpriteGrounded(g, fleaSheet, fleaFrame, fleaX + 22, groundY, 94);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            drawSpriteGrounded(g, fleaSheet, fleaFrame, fleaX + 42, groundY, 94);

            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    private void drawDust(Graphics2D g, int defenderX, int fleaX, int groundY) {
        boolean defenderMoving = "defender_attack".equals(currentAnimation) && frame >= 7 && frame <= 24;
        boolean fleaMoving = fleaVisible && "flea_attack".equals(currentAnimation) && frame >= 7 && frame <= 24;

        if (defenderMoving) {
            drawDustCloud(g, defenderX - 32, groundY + 2, -1);
        }

        if (fleaMoving) {
            drawDustCloud(g, fleaX + 38, groundY + 2, 1);
        }
    }

    private void drawDustCloud(Graphics2D g, int x, int y, int direction) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
        g.setColor(new Color(201, 166, 91));

        for (int i = 0; i < 7; i++) {
            int dustX = x + direction * ((frame + i * 6) % 36);
            int dustY = y - (i % 3) * 4;
            int size = 4 + i % 3;

            g.fillRect(dustX, dustY, size, size);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawEffects(Graphics2D g, Rectangle defenderBounds, Rectangle fleaBounds) {
        int defenderCenterX = defenderBounds.x + defenderBounds.width / 2;
        int defenderCenterY = defenderBounds.y + defenderBounds.height / 2;

        int fleaCenterX = fleaBounds.x + fleaBounds.width / 2;
        int fleaCenterY = fleaBounds.y + fleaBounds.height / 2;

        if ("defender_attack".equals(currentAnimation) && fleaVisible) {
            drawSlash(g, fleaCenterX - 20, fleaCenterY - 25, false);
            drawImpactSpark(g, fleaCenterX + 8, fleaCenterY - 8);
            drawFloatingText(g, "HIT!", fleaCenterX - 24, fleaBounds.y - 8, new Color(255, 230, 90));
        }

        if ("flea_attack".equals(currentAnimation) && fleaVisible) {
            drawSlash(g, defenderCenterX + 18, defenderCenterY - 30, true);
            drawImpactSpark(g, defenderCenterX + 4, defenderCenterY - 10);
            drawFloatingText(g, "DAMAGE!", defenderCenterX - 42, defenderBounds.y - 10, new Color(255, 86, 72));
        }

        if ("defender_defend".equals(currentAnimation)) {
            drawShieldAura(g, defenderCenterX, defenderCenterY, new Color(55, 125, 230));
            drawFloatingText(g, "DEFEND", defenderCenterX - 34, defenderBounds.y - 12, new Color(100, 180, 255));
        }

        if ("flea_defend".equals(currentAnimation) && fleaVisible) {
            drawShieldAura(g, fleaCenterX, fleaCenterY, new Color(175, 72, 180));
            drawFloatingText(g, "FLEA", fleaCenterX - 22, fleaBounds.y - 10, new Color(235, 160, 255));
        }

        if ("heal".equals(currentAnimation)) {
            drawHealEffect(g, defenderCenterX, defenderBounds.y + 26);
            drawFloatingText(g, "HEAL", defenderCenterX - 22, defenderBounds.y - 12, new Color(80, 230, 110));
        }
    }

    private void drawSlash(Graphics2D g, int x, int y, boolean reverse) {
        if (frame < 9 || frame > 19) {
            return;
        }

        float alpha = Math.max(0f, 1f - Math.abs(14 - frame) / 7f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 248, 176));

        if (reverse) {
            g.drawLine(x + 56, y, x + 8, y + 48);
            g.drawLine(x + 66, y + 16, x + 18, y + 64);
        } else {
            g.drawLine(x, y, x + 56, y + 48);
            g.drawLine(x - 10, y + 16, x + 46, y + 64);
        }

        g.setColor(new Color(255, 210, 64));

        if (reverse) {
            g.drawLine(x + 48, y + 6, x + 18, y + 36);
        } else {
            g.drawLine(x + 8, y + 6, x + 38, y + 36);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawImpactSpark(Graphics2D g, int x, int y) {
        if (frame < 11 || frame > 20) {
            return;
        }

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g.setColor(new Color(255, 239, 104));

        for (int i = 0; i < 10; i++) {
            double angle = i * Math.PI * 2 / 10;
            int distance = (frame - 10) * 3 + i % 3;
            int sparkX = x + (int) (Math.cos(angle) * distance);
            int sparkY = y + (int) (Math.sin(angle) * distance);
            int size = 3 + i % 2;

            g.fillRect(sparkX, sparkY, size, size);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawShieldAura(Graphics2D g, int x, int y, Color color) {
        float pulse = (float) Math.abs(Math.sin(frame * 0.24));
        float alpha = 0.25f + pulse * 0.35f;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);
        g.setStroke(new BasicStroke(5f));
        g.drawOval(x - 48, y - 48, 96, 96);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x - 60, y - 60, 120, 120);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawHealEffect(Graphics2D g, int x, int y) {
        float alpha = Math.max(0.15f, 1f - frame / 32f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(new Color(75, 235, 108));

        for (int i = 0; i < 7; i++) {
            int offsetX = (i - 3) * 18;
            int wave = (int) (Math.sin((frame + i * 8) * 0.24) * 8);
            int offsetY = -frame - (i % 2) * 8 + wave;
            drawPlus(g, x + offsetX, y + offsetY, 9);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawPlus(Graphics2D g, int x, int y, int size) {
        g.fillRect(x - size / 2, y - size * 2, size, size * 4);
        g.fillRect(x - size * 2, y - size / 2, size * 4, size);
    }

    private void drawFloatingText(Graphics2D g, String text, int x, int y, Color color) {
        int duration = Math.max(1, getAnimationDuration(currentAnimation));
        float alpha = Math.max(0f, 1f - frame / (float) duration);
        int floatY = y - frame;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setFont(new Font("Arial", Font.BOLD, 20));

        g.setColor(Color.BLACK);
        g.drawString(text, x + 2, floatY + 2);

        g.setColor(color);
        g.drawString(text, x, floatY);

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawCaption(Graphics2D g, int width) {
        String caption = getCaption();

        g.setFont(new Font("Arial", Font.BOLD, 14));

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(caption);

        g.setColor(Color.BLACK);
        g.drawString(caption, (width - textWidth) / 2 + 2, 28);

        g.setColor(Color.WHITE);
        g.drawString(caption, (width - textWidth) / 2, 26);
    }

    private String getCaption() {
        if ("defender_attack".equals(currentAnimation)) {
            return "Defender menyerang Flea";
        }

        if ("flea_attack".equals(currentAnimation)) {
            return "Flea menyerang setiap detik";
        }

        if ("defender_defend".equals(currentAnimation)) {
            return "Defender bertahan";
        }

        if ("flea_defend".equals(currentAnimation)) {
            return fleaVisible ? "Flea muncul di arena" : "Menunggu Flea berikutnya";
        }

        if ("heal".equals(currentAnimation)) {
            return "Defender memulihkan HP";
        }

        return fleaVisible ? "Pertarungan di Hutan" : "Menunggu Flea muncul";
    }

    private int getDefenderOffset() {
        if (!"defender_attack".equals(currentAnimation)) {
            return 0;
        }

        double t = frame / 28.0;

        if (t < 0.20) {
            return (int) (-10 * easeOut(t / 0.20));
        }

        if (t < 0.55) {
            return (int) (-10 + 110 * easeOut((t - 0.20) / 0.35));
        }

        if (t < 0.72) {
            return 100;
        }

        return (int) (100 * (1.0 - easeInOut((t - 0.72) / 0.28)));
    }

    private int getFleaOffset() {
        if (!"flea_attack".equals(currentAnimation)) {
            return 0;
        }

        double t = frame / 28.0;

        if (t < 0.20) {
            return (int) (10 * easeOut(t / 0.20));
        }

        if (t < 0.55) {
            return (int) (10 - 110 * easeOut((t - 0.20) / 0.35));
        }

        if (t < 0.72) {
            return -100;
        }

        return (int) (-100 * (1.0 - easeInOut((t - 0.72) / 0.28)));
    }

    private int getScreenShakeX() {
        boolean defenderImpact = "defender_attack".equals(currentAnimation) && frame >= 11 && frame <= 18;
        boolean fleaImpact = "flea_attack".equals(currentAnimation) && frame >= 11 && frame <= 18;

        if (defenderImpact || fleaImpact) {
            return frame % 2 == 0 ? 3 : -3;
        }

        return 0;
    }

    private double easeOut(double t) {
        double fixed = clamp(t);
        return 1.0 - Math.pow(1.0 - fixed, 3.0);
    }

    private double easeInOut(double t) {
        double fixed = clamp(t);

        if (fixed < 0.5) {
            return 4.0 * fixed * fixed * fixed;
        }

        return 1.0 - Math.pow(-2.0 * fixed + 2.0, 3.0) / 2.0;
    }

    private double clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }

        if (value > 1.0) {
            return 1.0;
        }

        return value;
    }
}
