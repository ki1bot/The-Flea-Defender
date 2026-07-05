package gui;

public class BattleRenderState {
    private final int width;
    private final int height;
    private final int idleTick;
    private final int frame;
    private final BattleAnimation animation;
    private final boolean fleaVisible;
    private final boolean defenderDead;

    public BattleRenderState(int width, int height, int idleTick, int frame, BattleAnimation animation, boolean fleaVisible, boolean defenderDead) {
        this.width = width;
        this.height = height;
        this.idleTick = idleTick;
        this.frame = frame;
        this.animation = animation;
        this.fleaVisible = fleaVisible;
        this.defenderDead = defenderDead;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getIdleTick() {
        return idleTick;
    }

    public int getFrame() {
        return frame;
    }

    public BattleAnimation getAnimation() {
        return animation;
    }

    public boolean isFleaVisible() {
        return fleaVisible;
    }

    public boolean isDefenderDead() {
        return defenderDead;
    }
}
