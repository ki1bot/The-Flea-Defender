package model;

import java.util.Random;

public class Flea extends GameCharacter {
    private final Random random;
    private final int minDamage;
    private final int maxDamage;
    private final int rewardPoint;

    public Flea() {
        this("Flea Normal", 45, 2, 4, 15);
    }

    public Flea(String name, int maxHp, int minDamage, int maxDamage, int rewardPoint) {
        super(name, maxHp);
        this.random = new Random();
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.rewardPoint = rewardPoint;
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
