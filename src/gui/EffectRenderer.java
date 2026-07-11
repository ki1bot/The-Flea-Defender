package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class EffectRenderer {
    public void drawShadow(Graphics2D g, int centerX, int centerY, int width, int height) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        g.setColor(Color.BLACK);
        g.fillOval(centerX - width / 2, centerY - height / 2, width, height);
        g.setComposite(AlphaComposite.SrcOver);
    }

    public void drawDust(Graphics2D g, BattleAnimation animation, int frame, int defenderX, int fleaX, int groundY, boolean fleaVisible) {
        boolean defenderMoving = animation == BattleAnimation.DEFENDER_ATTACK && frame >= 7 && frame <= 24;
        boolean defenderTraining = animation == BattleAnimation.DEFENDER_TRAIN && frame >= 7 && frame <= 36;
        boolean defenderBracing = animation == BattleAnimation.DEFENDER_DEFEND && frame >= 4 && frame <= 22;
        boolean fleaMoving = fleaVisible && animation == BattleAnimation.FLEA_ATTACK && frame >= 7 && frame <= 24;
        boolean fleaEntering = animation == BattleAnimation.FLEA_ENTER;
        boolean fleaDying = animation == BattleAnimation.FLEA_DEATH;
        boolean defenderDying = animation == BattleAnimation.DEFENDER_DEATH;

        if (defenderMoving || defenderTraining || defenderDying || defenderBracing) {
            drawDustCloud(g, frame, defenderX - 32, groundY + 2, -1);
        }

        if (fleaMoving || fleaEntering || fleaDying) {
            drawDustCloud(g, frame, fleaX + 38, groundY + 2, 1);
        }
    }

    public void drawBattleEffects(Graphics2D g, BattleAnimation animation, int frame, Rectangle defenderBounds, Rectangle fleaBounds, boolean fleaVisible, int panelWidth, int panelHeight) {
        int defenderCenterX = defenderBounds.x + defenderBounds.width / 2;
        int defenderCenterY = defenderBounds.y + defenderBounds.height / 2;

        int fleaCenterX = fleaBounds.x + fleaBounds.width / 2;
        int fleaCenterY = fleaBounds.y + fleaBounds.height / 2;

        if (animation == BattleAnimation.DEFENDER_ATTACK && fleaVisible) {
            drawSlash(g, frame, fleaCenterX - 20, fleaCenterY - 25, false);
            drawImpactSpark(g, frame, fleaCenterX + 8, fleaCenterY - 8);
            drawFloatingText(g, frame, animation.getDuration(), "HIT!", fleaCenterX - 24, fleaBounds.y - 8, new Color(255, 230, 90));
        }

        if (animation == BattleAnimation.DEFENDER_TRAIN) {
            drawTrainingEffect(g, frame, defenderBounds);
            drawFloatingText(g, frame, animation.getDuration(), "LATIHAN", defenderCenterX - 42, defenderBounds.y - 12, new Color(255, 220, 90));
        }

        if (animation == BattleAnimation.DEFENDER_VITAMIN) {
            drawVitaminEffect(g, frame, defenderBounds);
            drawFloatingText(g, frame, animation.getDuration(), "VITAMIN", defenderCenterX - 42, defenderBounds.y - 12, new Color(80, 240, 120));
        }

        if (animation == BattleAnimation.FLEA_ATTACK && fleaVisible) {
            drawSlash(g, frame, defenderCenterX + 18, defenderCenterY - 30, true);
            drawImpactSpark(g, frame, defenderCenterX + 4, defenderCenterY - 10);
            drawFloatingText(g, frame, animation.getDuration(), "DAMAGE!", defenderCenterX - 42, defenderBounds.y - 10, new Color(255, 86, 72));
        }

        if (animation == BattleAnimation.DEFENDER_DEFEND) {
            drawDefenderGuard(g, frame, defenderBounds);
            drawFloatingText(g, frame, animation.getDuration(), "GUARD", defenderCenterX - 30, defenderBounds.y - 12, new Color(100, 190, 255));
        }

        if (animation == BattleAnimation.FLEA_ENTER) {
            drawFloatingText(g, frame, animation.getDuration(), "FLEA DATANG", fleaCenterX - 54, fleaBounds.y - 8, new Color(235, 160, 255));
        }

        if (animation == BattleAnimation.FLEA_DEFEND && fleaVisible) {
            drawShieldAura(g, frame, fleaCenterX, fleaCenterY, new Color(175, 72, 180));
        }

        if (animation == BattleAnimation.FLEA_DEATH) {
            drawDeathExplosion(g, frame, animation.getDuration(), fleaCenterX, fleaCenterY, new Color(210, 70, 210));
            drawFloatingText(g, frame, animation.getDuration(), "FLEA MATI", fleaCenterX - 44, fleaBounds.y - 8, new Color(255, 180, 255));
        }

        if (animation == BattleAnimation.DEFENDER_DEATH) {
            drawDeathExplosion(g, frame, animation.getDuration(), defenderCenterX, defenderCenterY, new Color(255, 70, 70));
            drawFloatingText(g, frame, animation.getDuration(), "DEFENDER MATI", defenderCenterX - 66, defenderBounds.y - 12, new Color(255, 100, 100));
        }

        if (animation == BattleAnimation.HEAL) {
            drawHealEffect(g, frame, defenderCenterX, defenderBounds.y + 26);
            drawFloatingText(g, frame, animation.getDuration(), "HEAL", defenderCenterX - 22, defenderBounds.y - 12, new Color(80, 230, 110));
        }

        if (animation == BattleAnimation.VICTORY) {
            drawVictoryEffect(g, frame, panelWidth, panelHeight, defenderCenterX, defenderCenterY);
        }

        if (animation == BattleAnimation.GAME_OVER) {
            drawGameOverEffect(g, frame, panelWidth, panelHeight, defenderCenterX, defenderCenterY);
        }
    }

    private void drawVictoryEffect(Graphics2D g, int frame, int panelWidth, int panelHeight, int defenderX, int defenderY) {
        // Golden glow behind defender that grows outward
        if (frame >= 5) {
            float glowAlpha = Math.min(0.45f, (frame - 5) / 30f);
            int glowRadius = 30 + frame * 2;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
            g.setColor(new Color(255, 215, 0));
            g.fillOval(defenderX - glowRadius, defenderY - glowRadius, glowRadius * 2, glowRadius * 2);
            g.setColor(new Color(255, 245, 150));
            int innerRadius = glowRadius / 2;
            g.fillOval(defenderX - innerRadius, defenderY - innerRadius, innerRadius * 2, innerRadius * 2);
            g.setComposite(AlphaComposite.SrcOver);
        }

        // Fireworks bursting from multiple points
        if (frame >= 10) {
            drawFirework(g, frame - 10, panelWidth / 5, panelHeight / 4, new Color(255, 100, 100));
            drawFirework(g, frame - 15, panelWidth * 4 / 5, panelHeight / 3, new Color(100, 200, 255));
            drawFirework(g, frame - 20, panelWidth / 2, panelHeight / 5, new Color(255, 230, 80));
            drawFirework(g, frame - 30, panelWidth / 3, panelHeight / 2, new Color(130, 255, 130));
            drawFirework(g, frame - 38, panelWidth * 2 / 3, panelHeight / 4, new Color(255, 170, 255));
        }

        // Rising golden sparkles around defender
        if (frame >= 8) {
            float sparkleAlpha = Math.min(0.85f, (frame - 8) / 15f);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sparkleAlpha));

            for (int i = 0; i < 16; i++) {
                double angle = (i * Math.PI * 2 / 16) + frame * 0.06;
                int radius = 50 + (int) (Math.sin(frame * 0.15 + i) * 20) + frame;
                int sparkX = defenderX + (int) (Math.cos(angle) * radius);
                int sparkY = defenderY + (int) (Math.sin(angle) * radius) - frame;

                if (i % 3 == 0) {
                    g.setColor(new Color(255, 215, 0));
                } else if (i % 3 == 1) {
                    g.setColor(new Color(255, 245, 180));
                } else {
                    g.setColor(new Color(255, 190, 50));
                }

                int starSize = 3 + (frame + i) % 4;
                drawStar(g, sparkX, sparkY, starSize);
            }

            g.setComposite(AlphaComposite.SrcOver);
        }

        // Radiant rings expanding outward
        if (frame >= 15 && frame < 65) {
            for (int ring = 0; ring < 3; ring++) {
                int ringFrame = frame - 15 - ring * 10;

                if (ringFrame > 0 && ringFrame < 40) {
                    float ringAlpha = Math.max(0f, 0.6f - ringFrame / 40f);
                    int ringRadius = ringFrame * 5;

                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ringAlpha));
                    g.setColor(new Color(255, 230, 100));
                    g.setStroke(new BasicStroke(3f));
                    g.drawOval(defenderX - ringRadius, defenderY - ringRadius, ringRadius * 2, ringRadius * 2);
                    g.setComposite(AlphaComposite.SrcOver);
                }
            }
        }

        // Victory banner
        if (frame >= 20) {
            float bannerAlpha = Math.min(1f, (frame - 20) / 15f);
            drawBanner(g, panelWidth, panelHeight, "VICTORY!", bannerAlpha, frame,
                    new Color(255, 200, 0), new Color(255, 160, 0), new Color(80, 50, 0));
        }
    }

    private void drawGameOverEffect(Graphics2D g, int frame, int panelWidth, int panelHeight, int defenderX, int defenderY) {
        // Dark vignette overlay that gradually covers the screen
        if (frame >= 5) {
            float overlayAlpha = Math.min(0.55f, (frame - 5) / 40f);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayAlpha));
            g.setColor(new Color(20, 0, 0));
            g.fillRect(0, 0, panelWidth, panelHeight);
            g.setComposite(AlphaComposite.SrcOver);
        }

        // Red pulse flash
        if (frame >= 8 && frame < 50) {
            float pulseAlpha = (float) (Math.sin(frame * 0.25) * 0.15);
            if (pulseAlpha > 0) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha));
                g.setColor(new Color(200, 0, 0));
                g.fillRect(0, 0, panelWidth, panelHeight);
                g.setComposite(AlphaComposite.SrcOver);
            }
        }

        // Falling ember particles
        if (frame >= 10) {
            float emberAlpha = Math.min(0.75f, (frame - 10) / 20f);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, emberAlpha));

            for (int i = 0; i < 24; i++) {
                int emberX = (i * 83 + frame * (1 + i % 3)) % panelWidth;
                int emberY = (i * 47 + frame * (2 + i % 2)) % panelHeight;
                int sway = (int) (Math.sin(frame * 0.1 + i) * 6);

                if (i % 3 == 0) {
                    g.setColor(new Color(255, 80, 30));
                } else if (i % 3 == 1) {
                    g.setColor(new Color(255, 140, 40));
                } else {
                    g.setColor(new Color(200, 50, 20));
                }

                int size = 2 + i % 3;
                g.fillRect(emberX + sway, emberY, size, size);

                // Small glow around each ember
                if (i % 2 == 0) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, emberAlpha * 0.3f));
                    g.fillOval(emberX + sway - 2, emberY - 2, size + 4, size + 4);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, emberAlpha));
                }
            }

            g.setComposite(AlphaComposite.SrcOver);
        }

        // Skull crossbones at center
        if (frame >= 15 && frame < 70) {
            float skullAlpha = Math.min(0.7f, (frame - 15) / 20f);
            if (frame > 55) {
                skullAlpha = Math.max(0f, skullAlpha - (frame - 55) / 15f);
            }

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, skullAlpha));

            int skullX = panelWidth / 2;
            int skullY = panelHeight / 2 - 20;
            int bounce = (int) (Math.sin(frame * 0.12) * 4);

            // Skull circle
            g.setColor(new Color(180, 180, 180));
            g.fillOval(skullX - 22, skullY - 22 + bounce, 44, 48);

            // Eye sockets
            g.setColor(new Color(40, 0, 0));
            g.fillOval(skullX - 14, skullY - 10 + bounce, 12, 14);
            g.fillOval(skullX + 2, skullY - 10 + bounce, 12, 14);

            // Nose
            g.fillOval(skullX - 4, skullY + 6 + bounce, 8, 6);

            // Mouth line
            g.setStroke(new BasicStroke(2f));
            g.drawLine(skullX - 10, skullY + 18 + bounce, skullX + 10, skullY + 18 + bounce);
            for (int i = 0; i < 4; i++) {
                int lineX = skullX - 8 + i * 6;
                g.drawLine(lineX, skullY + 14 + bounce, lineX, skullY + 22 + bounce);
            }

            // Crossbones
            g.setColor(new Color(160, 160, 160));
            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(skullX - 36, skullY + 28 + bounce, skullX + 36, skullY + 58 + bounce);
            g.drawLine(skullX + 36, skullY + 28 + bounce, skullX - 36, skullY + 58 + bounce);

            // Bone ends
            int[][] ends = {{-36, 28}, {36, 28}, {-36, 58}, {36, 58}};
            for (int[] end : ends) {
                g.fillOval(skullX + end[0] - 5, skullY + end[1] + bounce - 5, 10, 10);
            }

            g.setComposite(AlphaComposite.SrcOver);
        }

        // Cracks spreading from center
        if (frame >= 12 && frame < 60) {
            float crackAlpha = Math.min(0.5f, (frame - 12) / 20f);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, crackAlpha));
            g.setColor(new Color(100, 0, 0));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = panelWidth / 2;
            int cy = panelHeight / 2;
            int reach = (frame - 12) * 4;

            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4 + 0.3;
                int endX = cx + (int) (Math.cos(angle) * reach);
                int endY = cy + (int) (Math.sin(angle) * reach);
                int midX = cx + (int) (Math.cos(angle + 0.2) * reach / 2);
                int midY = cy + (int) (Math.sin(angle - 0.1) * reach / 2);

                g.drawLine(cx, cy, midX, midY);
                g.drawLine(midX, midY, endX, endY);
            }

            g.setComposite(AlphaComposite.SrcOver);
        }

        // Game Over banner
        if (frame >= 25) {
            float bannerAlpha = Math.min(1f, (frame - 25) / 15f);
            drawBanner(g, panelWidth, panelHeight, "GAME OVER", bannerAlpha, frame,
                    new Color(180, 20, 20), new Color(120, 0, 0), new Color(255, 200, 200));
        }
    }

    private void drawFirework(Graphics2D g, int frame, int x, int y, Color color) {
        if (frame < 0 || frame > 30) {
            return;
        }

        float alpha = Math.max(0f, 1f - frame / 30f);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int numParticles = 14;
        for (int i = 0; i < numParticles; i++) {
            double angle = i * Math.PI * 2 / numParticles;
            int distance = frame * 4 + (i % 3) * 2;
            int px = x + (int) (Math.cos(angle) * distance);
            int py = y + (int) (Math.sin(angle) * distance) + frame / 3;
            int size = Math.max(1, 4 - frame / 10);

            g.setColor(color);
            g.fillRect(px, py, size, size);

            // Trail
            if (frame > 3) {
                int trailDist = distance - 8;
                int tx = x + (int) (Math.cos(angle) * trailDist);
                int ty = y + (int) (Math.sin(angle) * trailDist) + (frame - 2) / 3;
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                g.fillRect(tx, ty, Math.max(1, size - 1), Math.max(1, size - 1));
            }
        }

        // Center flash
        if (frame < 6) {
            g.setColor(Color.WHITE);
            int flashSize = 8 - frame;
            g.fillOval(x - flashSize, y - flashSize, flashSize * 2, flashSize * 2);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawStar(Graphics2D g, int x, int y, int size) {
        g.fillRect(x - size / 2, y, size, 1);
        g.fillRect(x, y - size / 2, 1, size);
        g.fillRect(x - size / 4, y - size / 4, size / 2, size / 2);
    }

    private void drawBanner(Graphics2D g, int panelWidth, int panelHeight, String text, float alpha, int frame, Color topColor, Color bottomColor, Color textColor) {
        int bannerHeight = 52;
        int bannerY = panelHeight / 2 - bannerHeight / 2 + 50;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.85f));
        GradientPaint gradient = new GradientPaint(0, bannerY, topColor, 0, bannerY + bannerHeight, bottomColor);
        g.setPaint(gradient);
        g.fillRect(0, bannerY, panelWidth, bannerHeight);

        // Border lines
        g.setColor(new Color(255, 255, 255, (int) (alpha * 180)));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(0, bannerY, panelWidth, bannerY);
        g.drawLine(0, bannerY + bannerHeight, panelWidth, bannerY + bannerHeight);

        // Shimmer effect
        int shimmerX = (frame * 8) % (panelWidth + 100) - 50;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.25f));
        g.setColor(Color.WHITE);
        g.fillRect(shimmerX, bannerY, 30, bannerHeight);
        g.fillRect(shimmerX + 40, bannerY, 15, bannerHeight);

        // Text
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = (panelWidth - textWidth) / 2;
        int textY = bannerY + bannerHeight / 2 + fm.getAscent() / 2 - 2;

        // Text shadow
        g.setColor(new Color(0, 0, 0, (int) (alpha * 150)));
        g.drawString(text, textX + 2, textY + 2);

        // Main text
        g.setColor(textColor);
        g.drawString(text, textX, textY);

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawTrainingEffect(Graphics2D g, int frame, Rectangle defenderBounds) {
        int x = defenderBounds.x + defenderBounds.width + 8;
        int y = defenderBounds.y + defenderBounds.height / 2 - 34;

        if (frame >= 14 && frame <= 30) {
            float alpha = Math.max(0f, 1f - Math.abs(22 - frame) / 12f);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(255, 245, 150));
            g.drawLine(x - 52, y - 24, x + 40, y + 38);
            g.drawLine(x - 40, y + 35, x + 45, y - 15);

            g.setColor(new Color(255, 190, 64));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x - 30, y - 8, x + 30, y + 25);

            for (int i = 0; i < 8; i++) {
                int sparkX = x + (i - 4) * 10;
                int sparkY = y + (i % 3) * 10 - frame % 8;

                g.fillRect(sparkX, sparkY, 4, 4);
            }

            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    private void drawVitaminEffect(Graphics2D g, int frame, Rectangle defenderBounds) {
        int centerX = defenderBounds.x + defenderBounds.width / 2;
        int centerY = defenderBounds.y + defenderBounds.height / 2;

        float alpha = Math.max(0.15f, 1f - frame / 38f);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        drawPotionBottle(g, centerX + 46, centerY - 32, frame);

        g.setColor(new Color(70, 235, 120));

        for (int i = 0; i < 8; i++) {
            int offsetX = (i - 4) * 15;
            int wave = (int) (Math.sin((frame + i * 5) * 0.28) * 8);
            int offsetY = -frame - (i % 2) * 8 + wave + 20;
            drawPlus(g, centerX + offsetX, defenderBounds.y + 60 + offsetY, 7);
        }

        g.setColor(new Color(180, 255, 190));
        g.setStroke(new BasicStroke(3f));
        g.drawOval(centerX - 44 - frame / 3, centerY - 48 - frame / 4, 88 + frame / 2, 92 + frame / 2);

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawPotionBottle(Graphics2D g, int x, int y, int frame) {
        int bounce = frame % 6 < 3 ? -2 : 2;

        g.setColor(new Color(20, 60, 35));
        g.fillRect(x - 8, y - 16 + bounce, 16, 8);
        g.fillRect(x - 12, y - 8 + bounce, 24, 28);

        g.setColor(new Color(80, 230, 120));
        g.fillRect(x - 8, y - 4 + bounce, 16, 18);

        g.setColor(new Color(190, 255, 200));
        g.fillRect(x - 4, y + bounce, 4, 8);

        g.setColor(new Color(255, 255, 255));
        g.fillRect(x - 3, y + 3 + bounce, 6, 2);
        g.fillRect(x - 1, y + 1 + bounce, 2, 6);
    }

    private void drawDefenderGuard(Graphics2D g, int frame, Rectangle defenderBounds) {
        int guardX = defenderBounds.x + defenderBounds.width - 18;
        int guardY = defenderBounds.y + defenderBounds.height / 2;

        float pulse = (float) Math.abs(Math.sin(frame * 0.34));
        float alpha = 0.32f + pulse * 0.38f;

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g.setColor(new Color(75, 160, 255));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(guardX - 44, guardY - 58, 88, 116, -70, 140);

        g.setColor(new Color(185, 225, 255));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(guardX - 55, guardY - 70, 110, 140, -68, 136);

        g.setColor(new Color(255, 236, 120));
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(guardX - 68, guardY - 82, 136, 164, -66, 132);

        for (int i = 0; i < 8; i++) {
            int sparkX = guardX + 18 + (i % 2) * 10;
            int sparkY = guardY - 48 + i * 14;
            int size = 3 + i % 2;

            if ((frame + i) % 3 != 0) {
                g.fillRect(sparkX, sparkY, size, size);
            }
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawDustCloud(Graphics2D g, int frame, int x, int y, int direction) {
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

    private void drawSlash(Graphics2D g, int frame, int x, int y, boolean reverse) {
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

    private void drawImpactSpark(Graphics2D g, int frame, int x, int y) {
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

    private void drawDeathExplosion(Graphics2D g, int frame, int duration, int x, int y, Color color) {
        float alpha = Math.max(0f, 1f - frame / (float) duration);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);

        for (int i = 0; i < 22; i++) {
            double angle = i * Math.PI * 2 / 22;
            int distance = 8 + frame * 2 + i % 5;
            int particleX = x + (int) (Math.cos(angle) * distance);
            int particleY = y + (int) (Math.sin(angle) * distance);
            int size = 3 + i % 3;

            g.fillRect(particleX, particleY, size, size);
        }

        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawShieldAura(Graphics2D g, int frame, int x, int y, Color color) {
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

    private void drawHealEffect(Graphics2D g, int frame, int x, int y) {
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

    private void drawFloatingText(Graphics2D g, int frame, int duration, String text, int x, int y, Color color) {
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
}
