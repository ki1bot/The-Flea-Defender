package gui;

public enum BattleAnimation {
    IDLE(1),
    DEFENDER_ATTACK(28),
    DEFENDER_DEFEND(24),
    DEFENDER_DEATH(54),
    FLEA_ENTER(24),
    FLEA_ATTACK(28),
    FLEA_DEFEND(24),
    FLEA_DEATH(44),
    HEAL(32);

    private final int duration;

    BattleAnimation(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
