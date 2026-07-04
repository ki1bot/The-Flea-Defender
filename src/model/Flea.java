package model;

import java.util.Random;

public class Flea extends GameCharacter {
    private final Random random;

    public Flea() {
        super("Flea", 50);
        this.random = new Random();
    }

    @Override
    public int attack(GameCharacter target) {
        int damage = random.nextInt(16) + 10;
        target.takeDamage(damage);
        return damage;
    }

    public void respawn() {
        setHp(getMaxHp());
    }
}
