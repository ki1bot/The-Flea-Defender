package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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

    public void drawBattleEffects(Graphics2D g, BattleAnimation animation, int frame, Rectangle defenderBounds, Rectangle fleaBounds, boolean fleaVisible) {
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
