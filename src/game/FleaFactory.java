package game;

import model.Flea;

import java.util.Random;

public class FleaFactory {
    private final Random random;

    public FleaFactory(Random random) {
        this.random = random;
    }

    public Flea createRandomFlea() {
        int type = random.nextInt(3);

        if (type == 0) {
            return new Flea(
                    "Flea Lemah",
                    GameBalance.FLEA_WEAK_HP,
                    GameBalance.FLEA_WEAK_MIN_DAMAGE,
                    GameBalance.FLEA_WEAK_MAX_DAMAGE,
                    GameBalance.FLEA_WEAK_REWARD
            );
        }

        if (type == 1) {
            return new Flea(
                    "Flea Normal",
                    GameBalance.FLEA_NORMAL_HP,
                    GameBalance.FLEA_NORMAL_MIN_DAMAGE,
                    GameBalance.FLEA_NORMAL_MAX_DAMAGE,
                    GameBalance.FLEA_NORMAL_REWARD
            );
        }

        return new Flea(
                "Flea Kuat",
                GameBalance.FLEA_STRONG_HP,
                GameBalance.FLEA_STRONG_MIN_DAMAGE,
                GameBalance.FLEA_STRONG_MAX_DAMAGE,
                GameBalance.FLEA_STRONG_REWARD
        );
    }
}
