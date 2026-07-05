package model;

import java.util.Random;

public class Flea extends GameCharacter {
    private final Random random;
    private final int minDamage;
    private final int maxDamage;
    private final int rewardPoint;

    public Flea(String name, int maxHp, int minDamage, int maxDamage, int rewardPoint) {
        super(name, maxHp);
        this.random = new Random();
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.rewardPoint = rewardPoint;
    }

    public static Flea createRandom(Random random) {
        int type = random.nextInt(3);

        if (type == 0) {
            return new Flea("Flea Lemah", 25, 6, 10, 10);
        }

        if (type == 1) {
            return new Flea("Flea Normal", 50, 10, 16, 20);
        }

        return new Flea("Flea Kuat", 80, 14, 22, 30);
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getRewardPoint() {
        return rewardPoint;
    }

    public int rollAttackDamage() {
        return random.nextInt(maxDamage - minDamage + 1) + minDamage;
    }

    @Override
    public int attack(GameCharacter target) {
        int damage = rollAttackDamage();
        target.takeDamage(damage);
        return damage;
    }
}
