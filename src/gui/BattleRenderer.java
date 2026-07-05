package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class BattleRenderer {
    private final BattleAssets assets;
    private final BackgroundRenderer backgroundRenderer;
    private final EffectRenderer effectRenderer;

    public BattleRenderer(BattleAssets assets) {
        this.assets = assets;
        this.backgroundRenderer = new BackgroundRenderer();
        this.effectRenderer = new EffectRenderer();
    }

    public void render(Graphics2D g, BattleRenderState state) {
        int width = state.getWidth();
        int height = state.getHeight();
        BattleAnimation animation = state.getAnimation();

        backgroundRenderer.draw(g, assets.getForestBackground(), width, height, state.getIdleTick());
        backgroundRenderer.drawFallingLeaves(g, width, height, state.getIdleTick());

        int groundY = (int) (height * 0.735);
        int defenderBaseX = width / 4;
        int fleaBaseX = width * 3 / 4;

        int defenderX = defenderBaseX + getDefenderOffset(animation, state.getFrame());
        int fleaX = fleaBaseX + getFleaOffset(animation, state.getFrame());

        int shakeX = getScreenShakeX(animation, state.getFrame());

        g.translate(shakeX, 0);

        int defenderFrame = getDefenderSpriteFrame(state);
        int fleaFrame = getFleaSpriteFrame(state);

        Rectangle defenderBounds = assets.getDefenderSheet().calculateDestination(defenderFrame, defenderX, groundY, 118);
        Rectangle fleaBounds = assets.getFleaSheet().calculateDestination(fleaFrame, fleaX, groundY, 94);

        if (getDefenderAlpha(state) > 0.05f) {
            effectRenderer.drawShadow(g, defenderX, groundY + 4, Math.max(48, defenderBounds.width - 26), 12);
        }

        if (shouldDrawFlea(state) && getFleaAlpha(state) > 0.05f) {
            effectRenderer.drawShadow(g, fleaX, groundY + 4, Math.max(54, fleaBounds.width - 22), 13);
        }

        drawMotionTrail(g, state, defenderFrame, fleaFrame, defenderX, fleaX, groundY);

        assets.getDefenderSheet().drawGrounded(g, defenderFrame, defenderX, groundY, 118, getDefenderAlpha(state));

        if (shouldDrawFlea(state)) {
            assets.getFleaSheet().drawGrounded(g, fleaFrame, fleaX, groundY, 94, getFleaAlpha(state));
        }

        effectRenderer.drawDust(g, animation, state.getFrame(), defenderX, fleaX, groundY, state.isFleaVisible());
        effectRenderer.drawBattleEffects(g, animation, state.getFrame(), defenderBounds, fleaBounds, state.isFleaVisible());

        g.translate(-shakeX, 0);

        drawCaption(g, width, getCaption(state));
    }

    private void drawMotionTrail(Graphics2D g, BattleRenderState state, int defenderFrame, int fleaFrame, int defenderX, int fleaX, int groundY) {
        BattleAnimation animation = state.getAnimation();
        int frame = state.getFrame();

        if (animation == BattleAnimation.DEFENDER_ATTACK && frame >= 8 && frame <= 20) {
            assets.getDefenderSheet().drawGrounded(g, defenderFrame, defenderX - 22, groundY, 118, 0.22f);
            assets.getDefenderSheet().drawGrounded(g, defenderFrame, defenderX - 42, groundY, 118, 0.12f);
        }

        if (animation == BattleAnimation.DEFENDER_TRAIN && frame >= 8 && frame <= 30) {
            assets.getDefenderSheet().drawGrounded(g, defenderFrame, defenderX - 14, groundY, 118, 0.16f);
            assets.getDefenderSheet().drawGrounded(g, defenderFrame, defenderX - 28, groundY, 118, 0.08f);
        }

        if (animation == BattleAnimation.DEFENDER_DEFEND && frame >= 5 && frame <= 24) {
            assets.getDefenderSheet().drawGrounded(g, defenderFrame, defenderX + 8, groundY, 118, 0.16f);
            assets.getDefenderSheet().drawGrounded(g, defenderFrame, defenderX + 16, groundY, 118, 0.08f);
        }

        if (state.isFleaVisible() && animation == BattleAnimation.FLEA_ATTACK && frame >= 8 && frame <= 20) {
            assets.getFleaSheet().drawGrounded(g, fleaFrame, fleaX + 22, groundY, 94, 0.22f);
            assets.getFleaSheet().drawGrounded(g, fleaFrame, fleaX + 42, groundY, 94, 0.12f);
        }
    }

    private boolean shouldDrawFlea(BattleRenderState state) {
        BattleAnimation animation = state.getAnimation();
        return state.isFleaVisible() || animation == BattleAnimation.FLEA_DEATH || animation == BattleAnimation.FLEA_ENTER;
    }

    private int getDefenderSpriteFrame(BattleRenderState state) {
        BattleAnimation animation = state.getAnimation();
        int frame = state.getFrame();

        if (animation == BattleAnimation.DEFENDER_DEATH) {
            if (frame < 16) {
                return 4;
            }

            if (frame < 36) {
                return 5;
            }

            return 6;
        }

        if (state.isDefenderDead()) {
            return 6;
        }

        if (animation == BattleAnimation.DEFENDER_ATTACK) {
            if (frame < 8) {
                return 1;
            }

            if (frame < 20) {
                return 2;
            }

            return 0;
        }

        if (animation == BattleAnimation.DEFENDER_TRAIN) {
            if (frame < 8) {
                return 0;
            }

            if (frame < 18) {
                return 1;
            }

            if (frame < 32) {
                return 2;
            }

            if (frame < 38) {
                return 1;
            }

            return 0;
        }

        if (animation == BattleAnimation.DEFENDER_VITAMIN) {
            if (frame < 8) {
                return 0;
            }

            if (frame < 25) {
                return 3;
            }

            return 0;
        }

        if (animation == BattleAnimation.DEFENDER_DEFEND) {
            if (frame < 6) {
                return 1;
            }

            if (frame < 34) {
                return 3;
            }

            return 0;
        }

        if (animation == BattleAnimation.HEAL) {
            return frame / 8 % 2 == 0 ? 0 : 3;
        }

        return state.getIdleTick() / 22 % 2 == 0 ? 0 : 1;
    }

    private int getFleaSpriteFrame(BattleRenderState state) {
        BattleAnimation animation = state.getAnimation();
        int frame = state.getFrame();

        if (animation == BattleAnimation.FLEA_DEATH) {
            if (frame < 12) {
                return 4;
            }

            if (frame < 28) {
                return 5;
            }

            return 6;
        }

        if (animation == BattleAnimation.FLEA_DEFEND) {
            return 4;
        }

        if (animation == BattleAnimation.FLEA_ATTACK) {
            if (frame < 8) {
                return 1;
            }

            if (frame < 20) {
                return 2;
            }

            return 3;
        }

        if (animation == BattleAnimation.FLEA_ENTER) {
            return frame / 6 % 2 == 0 ? 0 : 1;
        }

        return state.getIdleTick() / 24 % 2 == 0 ? 0 : 1;
    }

    private String getCaption(BattleRenderState state) {
        BattleAnimation animation = state.getAnimation();

        if (animation == BattleAnimation.DEFENDER_ATTACK) {
            return "Defender menyerang Flea";
        }

        if (animation == BattleAnimation.DEFENDER_TRAIN) {
            return "Defender berlatih menggunakan pedang";
        }

        if (animation == BattleAnimation.DEFENDER_VITAMIN) {
            return "Defender membeli dan memakai vitamin";
        }

        if (animation == BattleAnimation.FLEA_ATTACK) {
            return "Flea menyerang setiap detik";
        }

        if (animation == BattleAnimation.DEFENDER_DEFEND) {
            return "Defender memasang pertahanan";
        }

        if (animation == BattleAnimation.FLEA_ENTER) {
            return "Flea baru muncul";
        }

        if (animation == BattleAnimation.FLEA_DEATH) {
            return "Flea mati";
        }

        if (animation == BattleAnimation.DEFENDER_DEATH) {
            return "Defender mati";
        }

        if (animation == BattleAnimation.HEAL) {
            return "Defender memulihkan HP";
        }

        return state.isFleaVisible() ? "Pertarungan di Hutan" : "Menunggu Flea muncul";
    }

    private void drawCaption(Graphics2D g, int width, String caption) {
        g.setFont(new Font("Arial", Font.BOLD, 14));

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(caption);

        g.setColor(Color.BLACK);
        g.drawString(caption, (width - textWidth) / 2 + 2, 28);

        g.setColor(Color.WHITE);
        g.drawString(caption, (width - textWidth) / 2, 26);
    }

    private int getDefenderOffset(BattleAnimation animation, int frame) {
        if (animation == BattleAnimation.DEFENDER_TRAIN) {
            if (frame < 8) {
                return (int) (-8 * easeOut(frame / 8.0));
            }

            if (frame < 24) {
                return -8 + (frame % 4 < 2 ? -2 : 2);
            }

            if (frame < 34) {
                return (int) (12 * easeOut((frame - 24) / 10.0));
            }

            return (int) (12 * (1.0 - easeInOut((frame - 34) / 10.0)));
        }

        if (animation == BattleAnimation.DEFENDER_VITAMIN) {
            if (frame < 8) {
                return 0;
            }

            if (frame < 28) {
                return frame % 4 < 2 ? -1 : 1;
            }

            return 0;
        }

        if (animation == BattleAnimation.DEFENDER_DEFEND) {
            if (frame < 6) {
                return (int) (-12 * easeOut(frame / 6.0));
            }

            if (frame < 32) {
                return -12 + (frame % 4 < 2 ? -2 : 2);
            }

            return (int) (-12 * (1.0 - easeInOut((frame - 32) / 10.0)));
        }

        if (animation != BattleAnimation.DEFENDER_ATTACK) {
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

    private int getFleaOffset(BattleAnimation animation, int frame) {
        if (animation == BattleAnimation.FLEA_ENTER) {
            double t = frame / 24.0;
            return (int) (130 * (1.0 - easeOut(t)));
        }

        if (animation != BattleAnimation.FLEA_ATTACK) {
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

    private float getDefenderAlpha(BattleRenderState state) {
        if (state.getAnimation() == BattleAnimation.DEFENDER_DEATH) {
            return 1f;
        }

        if (state.isDefenderDead()) {
            return 0.8f;
        }

        return 1f;
    }

    private float getFleaAlpha(BattleRenderState state) {
        if (state.getAnimation() == BattleAnimation.FLEA_DEATH) {
            double t = state.getFrame() / (double) BattleAnimation.FLEA_DEATH.getDuration();

            if (t < 0.75) {
                return 1f;
            }

            return Math.max(0f, (float) (1.0 - easeOut((t - 0.75) / 0.25)));
        }

        return 1f;
    }

    private int getScreenShakeX(BattleAnimation animation, int frame) {
        boolean defenderImpact = animation == BattleAnimation.DEFENDER_ATTACK && frame >= 11 && frame <= 18;
        boolean fleaImpact = animation == BattleAnimation.FLEA_ATTACK && frame >= 11 && frame <= 18;
        boolean defenderDefend = animation == BattleAnimation.DEFENDER_DEFEND && frame >= 6 && frame <= 15;
        boolean defenderTrain = animation == BattleAnimation.DEFENDER_TRAIN && frame >= 18 && frame <= 28;
        boolean fleaDeath = animation == BattleAnimation.FLEA_DEATH && frame <= 18;
        boolean defenderDeath = animation == BattleAnimation.DEFENDER_DEATH && frame <= 20;

        if (defenderImpact || fleaImpact || fleaDeath || defenderDeath) {
            return frame % 2 == 0 ? 3 : -3;
        }

        if (defenderDefend || defenderTrain) {
            return frame % 2 == 0 ? 1 : -1;
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
