package gui;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayDeque;
import java.util.Deque;

public class BattlePanel extends JPanel {
    private final Deque<BattleAnimation> animationQueue;
    private final Timer animationTimer;
    private final BattleRenderer renderer;
    private final SoundManager soundManager;

    private BattleAnimation currentAnimation;
    private int frame;
    private int idleTick;
    private boolean fleaVisible;
    private boolean defenderDead;
    private Runnable animationFinishedListener;

    public BattlePanel() {
        animationQueue = new ArrayDeque<>();
        currentAnimation = BattleAnimation.IDLE;
        frame = 0;
        idleTick = 0;
        fleaVisible = false;
        defenderDead = false;

        BattleAssets assets = new AssetLoader().loadBattleAssets();
        renderer = new BattleRenderer(assets);
        soundManager = new SoundManager();

        setPreferredSize(new Dimension(900, 300));
        setMinimumSize(new Dimension(760, 260));

        animationTimer = new Timer(28, event -> updateAnimation());
        animationTimer.start();
    }

    public void setAnimationFinishedListener(Runnable animationFinishedListener) {
        this.animationFinishedListener = animationFinishedListener;
    }

    public void setFleaVisible(boolean fleaVisible) {
        this.fleaVisible = fleaVisible;
        repaint();
    }

    public void setDefenderDead(boolean defenderDead) {
        this.defenderDead = defenderDead;
        repaint();
    }

    public void setSoundEnabled(boolean enabled) {
        soundManager.setEnabled(enabled);
    }

    public boolean isSoundEnabled() {
        return soundManager.isEnabled();
    }

    public void playDefenderAttack() {
        enqueueAnimation(BattleAnimation.DEFENDER_ATTACK);
    }

    public void playDefenderTrain() {
        enqueueAnimation(BattleAnimation.DEFENDER_TRAIN);
    }

    public void playDefenderVitamin() {
        enqueueAnimation(BattleAnimation.DEFENDER_VITAMIN);
    }

    public void playDefenderDefend() {
        enqueueAnimation(BattleAnimation.DEFENDER_DEFEND);
    }

    public void playDefenderDeath() {
        enqueueAnimation(BattleAnimation.DEFENDER_DEATH);
    }

    public void playFleaEnter() {
        enqueueAnimation(BattleAnimation.FLEA_ENTER);
    }

    public void playFleaAttack() {
        enqueueAnimation(BattleAnimation.FLEA_ATTACK);
    }

    public void playFleaDefend() {
        enqueueAnimation(BattleAnimation.FLEA_DEFEND);
    }

    public void playFleaDeath() {
        enqueueAnimation(BattleAnimation.FLEA_DEATH);
    }

    public void playHeal() {
        enqueueAnimation(BattleAnimation.HEAL);
    }

    public void playVictory() {
        enqueueAnimation(BattleAnimation.VICTORY);
    }

    public void playGameOver() {
        enqueueAnimation(BattleAnimation.GAME_OVER);
    }

    public void clearAnimations() {
        animationQueue.clear();
        currentAnimation = BattleAnimation.IDLE;
        frame = 0;
        soundManager.stopAll();
        repaint();
    }

    public void disposeAudio() {
        soundManager.closeAll();
    }

    private void enqueueAnimation(BattleAnimation animation) {
        animationQueue.add(animation);

        if (currentAnimation == BattleAnimation.IDLE) {
            startNextAnimation();
        }
    }

    private void updateAnimation() {
        idleTick++;

        if (currentAnimation == BattleAnimation.IDLE) {
            if (!animationQueue.isEmpty()) {
                startNextAnimation();
            }

            repaint();
            return;
        }

        frame++;

        if (frame >= currentAnimation.getDuration()) {
            currentAnimation = BattleAnimation.IDLE;
            frame = 0;

            if (!animationQueue.isEmpty()) {
                startNextAnimation();
            } else {
                notifyAnimationFinished();
            }
        }

        repaint();
    }

    private void startNextAnimation() {
        BattleAnimation next = animationQueue.poll();

        if (next != null) {
            currentAnimation = next;
            frame = 0;
            soundManager.play(SoundEffect.fromAnimation(currentAnimation));
        }
    }

    private void notifyAnimationFinished() {
        if (animationFinishedListener != null) {
            SwingUtilities.invokeLater(animationFinishedListener);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        BattleRenderState state = new BattleRenderState(
                getWidth(),
                getHeight(),
                idleTick,
                frame,
                currentAnimation,
                fleaVisible,
                defenderDead
        );

        renderer.render(g, state);

        g.dispose();
    }
}
