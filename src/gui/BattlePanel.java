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

        setPreferredSize(new Dimension(760, 300));
        setMinimumSize(new Dimension(680, 260));
        setBackground(Color.BLACK);

        loadAssets();

        animationTimer = new Timer(25, event -> updateAnimation());
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

                boolean almostWhite = red > 232 && green > 232 && blue > 232;
                boolean lightGray = red > 215 && green > 215 && blue > 215 && Math.abs(red - green) < 14 && Math.abs(green - blue) < 14;

                if (almostWhite || lightGray) {
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
        if ("defender_attack".equals(animation)) {
            return 46;
        }

        if ("flea_attack".equals(animation)) {
            return 46;
        }

        if ("defender_defend".equals(animation) || "flea_defend".equals(animation)) {
            return 44;
        }

        if ("heal".equals(animation)) {
            return 52;
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

        int shakeX = getScreenShakeX();
        int shakeY = getScreenShakeY();

        g.translate(shakeX, shakeY);

        int groundY = height - 58;
        int defenderBaseX = Math.max(95, width / 4 - 40);
        int fleaBaseX = Math.min(width - 205, width * 3 / 4 - 65);

        int defenderOffset = getDefenderOffset();
        int fleaOffset = getFleaOffset();

        int defenderJump = getDefenderJump();
        int fleaJump = getFleaJump();

        int defenderIdleY = (int) (Math.sin(idleTick * 0.13) * 3);
        int fleaIdleY = (int) (Math.sin(idleTick * 0.16 + 1.4) * 4);

        int defenderX = defenderBaseX + defenderOffset;
        int defenderY = groundY - 150 + defenderIdleY + defenderJump;

        int fleaX = fleaBaseX + fleaOffset;
        int fleaY = groundY - 132 + fleaIdleY + fleaJump;

        drawShadow(g, defenderX + 76, groundY + 5, 84 - Math.abs(defenderJump), 14);
        drawShadow(g, fleaX + 88, groundY + 7, 116 - Math.abs(fleaJump), 16);

        drawMotionTrail(g, defenderX, defenderY, fleaX, fleaY);

        drawDefenderSprite(g, defenderX, defenderY);
        drawFleaSprite(g, fleaX, fleaY);

        drawDust(g, defenderX + 78, groundY, fleaX + 90, groundY);
        drawEffects(g, defenderX + 80, fleaX + 96, groundY);

        g.translate(-shakeX, -shakeY);

        drawCaption(g, width);

        g.dispose();
    }

    private void drawForestBackground(Graphics2D g, int width, int height) {
        if (forestBackground != null) {
            int extraWidth = 24;
            int parallaxX = (int) (Math.sin(idleTick * 0.01) * 8);

            g.drawImage(forestBackground, -extraWidth + parallaxX, 0, width + extraWidth * 2, height, null);
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
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));

        for (int i = 0; i < 18; i++) {
            int speed = 1 + i % 3;
            int x = (i * 91 + idleTick * speed) % (width + 40) - 20;
            int y = (i * 47 + idleTick * (speed + 1)) % Math.max(1, height - 48);

            if (i % 3 == 0) {
                g.setColor(new Color(92, 164, 48));
            } else if (i % 3 == 1) {
                g.setColor(new Color(126, 186, 52));
            } else {
                g.setColor(new Color(68, 128, 42));
            }

            int size = 3 + i % 2;
            g.fillRect(x, y, size + 2, size);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawDefenderSprite(Graphics2D g, int x, int y) {
        if (defenderSheet == null) {
            drawMissingSprite(g, x, y, "DEFENDER");
            return;
        }

        int spriteFrame = getDefenderSpriteFrame();
        drawSpriteFrame(g, defenderSheet, spriteFrame, x, y, 170, 150);
    }

    private void drawFleaSprite(Graphics2D g, int x, int y) {
        if (fleaSheet == null) {
            drawMissingSprite(g, x, y, "FLEA");
            return;
        }

        int spriteFrame = getFleaSpriteFrame();
        drawSpriteFrame(g, fleaSheet, spriteFrame, x, y, 190, 136);
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
            if (frame < 12) {
                return 1;
            }

            if (frame < 31) {
                return 2;
            }

            return 0;
        }

        if ("defender_defend".equals(currentAnimation)) {
            return 3;
        }

        if ("heal".equals(currentAnimation)) {
            return idleTick / 10 % 2 == 0 ? 0 : 3;
        }

        return 0;
    }

    private int getFleaSpriteFrame() {
        if ("flea_attack".equals(currentAnimation)) {
            if (frame < 12) {
                return 1;
            }

            if (frame < 31) {
                return 2;
            }

            return 0;
        }

        if ("flea_defend".equals(currentAnimation)) {
            return 3;
        }

        return idleTick / 22 % 2 == 0 ? 0 : 1;
    }

    private void drawMissingSprite(Graphics2D g, int x, int y, String text) {
        g.setColor(new Color(180, 30, 30));
        g.fillRect(x, y, 120, 80);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString(text, x + 18, y + 45);
    }

    private void drawShadow(Graphics2D g, int x, int y, int width, int height) {
        int fixedWidth = Math.max(40, width);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.38f));
        g.setColor(Color.BLACK);
        g.fillOval(x - fixedWidth / 2, y - height / 2, fixedWidth, height);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawMotionTrail(Graphics2D g, int defenderX, int defenderY, int fleaX, int fleaY) {
        if ("defender_attack".equals(currentAnimation) && frame >= 12 && frame <= 30 && defenderSheet != null) {
            int spriteFrame = getDefenderSpriteFrame();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
            drawSpriteFrame(g, defenderSheet, spriteFrame, defenderX - 24, defenderY, 170, 150);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            drawSpriteFrame(g, defenderSheet, spriteFrame, defenderX - 44, defenderY, 170, 150);

            g.setComposite(AlphaComposite.SrcOver);
        }

        if ("flea_attack".equals(currentAnimation) && frame >= 12 && frame <= 30 && fleaSheet != null) {
            int spriteFrame = getFleaSpriteFrame();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
            drawSpriteFrame(g, fleaSheet, spriteFrame, fleaX + 26, fleaY, 190, 136);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            drawSpriteFrame(g, fleaSheet, spriteFrame, fleaX + 48, fleaY, 190, 136);

            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    private void drawDust(Graphics2D g, int defenderFootX, int defenderFootY, int fleaFootX, int fleaFootY) {
        boolean defenderMoving = "defender_attack".equals(currentAnimation) && frame >= 7 && frame <= 36;
        boolean fleaMoving = "flea_attack".equals(currentAnimation) && frame >= 7 && frame <= 36;

        if (defenderMoving) {
            drawDustCloud(g, defenderFootX - 18, defenderFootY + 3, false);
        }

        if (fleaMoving) {
            drawDustCloud(g, fleaFootX + 18, fleaFootY + 3, true);
        }
    }

    private void drawDustCloud(Graphics2D g, int x, int y, boolean reverse) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
        g.setColor(new Color(201, 166, 91));

        for (int i = 0; i < 7; i++) {
            int direction = reverse ? 1 : -1;
            int dustX = x + direction * ((frame + i * 7) % 42);
            int dustY = y - (i % 3) * 5;
            int size = 4 + i % 3;

            g.fillRect(dustX, dustY, size, size);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawEffects(Graphics2D g, int defenderX, int fleaX, int groundY) {
        if ("defender_attack".equals(currentAnimation)) {
            drawSlash(g, fleaX - 16, groundY - 108, false);
            drawImpactSpark(g, fleaX + 20, groundY - 82);
            drawFloatingText(g, "HIT!", fleaX - 24, groundY - 134, new Color(255, 230, 90));
        }

        if ("flea_attack".equals(currentAnimation)) {
            drawSlash(g, defenderX + 42, groundY - 116, true);
            drawImpactSpark(g, defenderX + 24, groundY - 96);
            drawFloatingText(g, "DAMAGE!", defenderX - 36, groundY - 142, new Color(255, 86, 72));
        }

        if ("defender_defend".equals(currentAnimation)) {
            drawShieldAura(g, defenderX + 2, groundY - 86, new Color(55, 125, 230));
            drawFloatingText(g, "DEFEND", defenderX - 34, groundY - 142, new Color(100, 180, 255));
        }

        if ("flea_defend".equals(currentAnimation)) {
            drawShieldAura(g, fleaX + 6, groundY - 80, new Color(175, 72, 180));
            drawFloatingText(g, "RECOIL", fleaX - 28, groundY - 134, new Color(235, 160, 255));
        }

        if ("heal".equals(currentAnimation)) {
            drawHealEffect(g, defenderX, groundY - 104);
            drawFloatingText(g, "HEAL", defenderX - 20, groundY - 144, new Color(80, 230, 110));
        }
    }

    private void drawSlash(Graphics2D g, int x, int y, boolean reverse) {
        if (frame < 13 || frame > 25) {
            return;
        }

        float alpha = Math.max(0f, 1f - Math.abs(19 - frame) / 8f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.setColor(new Color(255, 248, 176));

        if (reverse) {
            g.drawLine(x + 68, y, x + 8, y + 58);
            g.drawLine(x + 80, y + 18, x + 20, y + 76);
        } else {
            g.drawLine(x, y, x + 68, y + 58);
            g.drawLine(x - 12, y + 18, x + 56, y + 76);
        }

        g.setColor(new Color(255, 210, 64));

        if (reverse) {
            g.drawLine(x + 58, y + 6, x + 16, y + 48);
        } else {
            g.drawLine(x + 8, y + 6, x + 50, y + 48);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawImpactSpark(Graphics2D g, int x, int y) {
        if (frame < 15 || frame > 25) {
            return;
        }

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        g.setColor(new Color(255, 239, 104));

        for (int i = 0; i < 10; i++) {
            double angle = i * Math.PI * 2 / 10;
            int distance = (frame - 14) * 3 + i % 3;
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
        g.drawOval(x - 58, y - 58, 116, 116);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x - 72, y - 72, 144, 144);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawHealEffect(Graphics2D g, int x, int y) {
        float alpha = Math.max(0.15f, 1f - frame / 52f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(new Color(75, 235, 108));

        for (int i = 0; i < 7; i++) {
            int offsetX = (i - 3) * 20;
            int wave = (int) (Math.sin((frame + i * 8) * 0.24) * 10);
            int offsetY = -frame - (i % 2) * 10 + wave;
            drawPlus(g, x + offsetX, y + offsetY, 10);
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
        g.setFont(new Font("Arial", Font.BOLD, 22));

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
            return "Flea menerkam Defender";
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

        double t = frame / 46.0;

        if (t < 0.18) {
            return (int) (-18 * easeOut(t / 0.18));
        }

        if (t < 0.58) {
            return (int) (-18 + 132 * easeOut((t - 0.18) / 0.40));
        }

        if (t < 0.72) {
            return 114;
        }

        return (int) (114 * (1.0 - easeInOut((t - 0.72) / 0.28)));
    }

    private int getFleaOffset() {
        if (!"flea_attack".equals(currentAnimation)) {
            return 0;
        }

        double t = frame / 46.0;

        if (t < 0.18) {
            return (int) (18 * easeOut(t / 0.18));
        }

        if (t < 0.58) {
            return (int) (18 - 132 * easeOut((t - 0.18) / 0.40));
        }

        if (t < 0.72) {
            return -114;
        }

        return (int) (-114 * (1.0 - easeInOut((t - 0.72) / 0.28)));
    }

    private int getDefenderJump() {
        if ("defender_attack".equals(currentAnimation)) {
            double t = frame / 46.0;
            return (int) (-14 * Math.sin(Math.PI * Math.min(1.0, t)));
        }

        if ("defender_defend".equals(currentAnimation)) {
            return frame % 8 < 4 ? 1 : -1;
        }

        return 0;
    }

    private int getFleaJump() {
        if ("flea_attack".equals(currentAnimation)) {
            double t = frame / 46.0;
            return (int) (-28 * Math.sin(Math.PI * Math.min(1.0, t)));
        }

        if ("flea_defend".equals(currentAnimation)) {
            return frame % 6 < 3 ? 2 : -2;
        }

        return 0;
    }

    private int getScreenShakeX() {
        boolean defenderImpact = "defender_attack".equals(currentAnimation) && frame >= 16 && frame <= 23;
        boolean fleaImpact = "flea_attack".equals(currentAnimation) && frame >= 16 && frame <= 23;

        if (defenderImpact || fleaImpact) {
            return frame % 2 == 0 ? 4 : -4;
        }

        return 0;
    }

    private int getScreenShakeY() {
        boolean defenderImpact = "defender_attack".equals(currentAnimation) && frame >= 16 && frame <= 23;
        boolean fleaImpact = "flea_attack".equals(currentAnimation) && frame >= 16 && frame <= 23;

        if (defenderImpact || fleaImpact) {
            return frame % 3 == 0 ? 2 : -2;
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
