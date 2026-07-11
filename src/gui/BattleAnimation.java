package gui;

public enum BattleAnimation {
    IDLE(1),
    DEFENDER_ATTACK(28),
    DEFENDER_TRAIN(44),
    DEFENDER_VITAMIN(38),
    DEFENDER_DEFEND(42),
    DEFENDER_DEATH(54),
    FLEA_ENTER(24),
    FLEA_ATTACK(28),
    FLEA_DEFEND(28),
    FLEA_DEATH(44),
    HEAL(32),
    VICTORY(80),
    GAME_OVER(90);

    private final int duration;

    BattleAnimation(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
