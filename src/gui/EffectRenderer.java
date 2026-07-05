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
        boolean fleaMoving = fleaVisible && animation == BattleAnimation.FLEA_ATTACK && frame >= 7 && frame <= 24;
        boolean fleaEntering = animation == BattleAnimation.FLEA_ENTER;
        boolean fleaDying = animation == BattleAnimation.FLEA_DEATH;
        boolean defenderDying = animation == BattleAnimation.DEFENDER_DEATH;

        if (defenderMoving || defenderDying) {
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

        if (animation == BattleAnimation.FLEA_ATTACK && fleaVisible) {
            drawSlash(g, frame, defenderCenterX + 18, defenderCenterY - 30, true);
            drawImpactSpark(g, frame, defenderCenterX + 4, defenderCenterY - 10);
            drawFloatingText(g, frame, animation.getDuration(), "DAMAGE!", defenderCenterX - 42, defenderBounds.y - 10, new Color(255, 86, 72));
        }

        if (animation == BattleAnimation.DEFENDER_DEFEND) {
            drawShieldAura(g, frame, defenderCenterX, defenderCenterY, new Color(55, 125, 230));
            drawFloatingText(g, frame, animation.getDuration(), "DEFEND", defenderCenterX - 34, defenderBounds.y - 12, new Color(100, 180, 255));
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
