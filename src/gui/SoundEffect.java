package gui;

public enum SoundEffect {
    DEFENDER_ATTACK("defender_attack.wav"),
    DEFENDER_DEFEND("defender_defend.wav"),
    DEFENDER_DEATH("defender_death.wav"),
    FLEA_ENTER("flea_enter.wav"),
    FLEA_ATTACK("flea_attack.wav"),
    FLEA_DEFEND("flea_defend.wav"),
    FLEA_DEATH("flea_death.wav"),
    HEAL("heal.wav");

    private final String fileName;

    SoundEffect(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public static SoundEffect fromAnimation(BattleAnimation animation) {
        if (animation == BattleAnimation.DEFENDER_ATTACK) {
            return DEFENDER_ATTACK;
        }

        if (animation == BattleAnimation.DEFENDER_TRAIN) {
            return DEFENDER_ATTACK;
        }

        if (animation == BattleAnimation.DEFENDER_VITAMIN) {
            return HEAL;
        }

        if (animation == BattleAnimation.DEFENDER_DEFEND) {
            return DEFENDER_DEFEND;
        }

        if (animation == BattleAnimation.DEFENDER_DEATH) {
            return DEFENDER_DEATH;
        }

        if (animation == BattleAnimation.FLEA_ENTER) {
            return FLEA_ENTER;
        }

        if (animation == BattleAnimation.FLEA_ATTACK) {
            return FLEA_ATTACK;
        }

        if (animation == BattleAnimation.FLEA_DEFEND) {
            return FLEA_DEFEND;
        }

        if (animation == BattleAnimation.FLEA_DEATH) {
            return FLEA_DEATH;
        }

        if (animation == BattleAnimation.HEAL) {
            return HEAL;
        }

        return null;
    }
}
