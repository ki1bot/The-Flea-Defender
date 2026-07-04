package model;

public abstract class GameCharacter {
    private final String name;
    private int hp;
    private final int maxHp;

    public GameCharacter(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) {
            hp = 0;
        }
    }

    public void heal(int amount) {
        hp += amount;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp > maxHp) {
            this.hp = maxHp;
        }
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public abstract int attack(GameCharacter target);
}
