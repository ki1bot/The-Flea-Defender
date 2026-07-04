package model;

import java.util.Random;

public class Defender extends GameCharacter {
    private int resourcePoint;
    private final Random random;
    private final int vitaminCost;
    private final int vitaminHeal;
    private int lastSelfDamage;

    public Defender() {
        super("Defender", 100);
        this.resourcePoint = 0;
        this.random = new Random();
        this.vitaminCost = 20;
        this.vitaminHeal = 30;
        this.lastSelfDamage = 0;
    }

    public int getResourcePoint() {
        return resourcePoint;
    }

    public int getVitaminCost() {
        return vitaminCost;
    }

    public int getVitaminHeal() {
        return vitaminHeal;
    }

    public int getLastSelfDamage() {
        return lastSelfDamage;
    }

    public void addResourcePoint(int point) {
        resourcePoint += point;
    }

    public void useResourcePoint(int point) {
        resourcePoint -= point;
        if (resourcePoint < 0) {
            resourcePoint = 0;
        }
    }

    @Override
    public int attack(GameCharacter target) {
        int damage = random.nextInt(16) + 15;
        lastSelfDamage = random.nextInt(6) + 5;
        target.takeDamage(damage);
        takeDamage(lastSelfDamage);
        return damage;
    }

    public int train() {
        int healAmount = random.nextInt(16) + 15;
        int beforeHp = getHp();
        heal(healAmount);
        return getHp() - beforeHp;
    }

    public int buyVitamin() {
        if (getHp() >= getMaxHp()) {
            return -2;
        }
        if (resourcePoint < vitaminCost) {
            return -1;
        }
        int beforeHp = getHp();
        useResourcePoint(vitaminCost);
        heal(vitaminHeal);
        return getHp() - beforeHp;
    }
}
