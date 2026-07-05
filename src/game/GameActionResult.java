package game;

public class GameActionResult {
    private final StringBuilder log;
    private boolean fleaSpawned;
    private boolean fleaAttacked;
    private boolean defenderAttacked;
    private boolean fleaKilled;
    private boolean defenderDied;
    private boolean healed;
    private boolean defended;
    private boolean gameFinished;

    public GameActionResult() {
        log = new StringBuilder();
    }

    public void addLog(String message) {
        log.append(message);
    }

    public String getMessage() {
        return log.toString();
    }

    public boolean hasMessage() {
        return log.length() > 0;
    }

    public boolean hasAnimationEvent() {
        return fleaSpawned || fleaAttacked || defenderAttacked || fleaKilled || defenderDied || healed || defended;
    }

    public boolean isFleaSpawned() {
        return fleaSpawned;
    }

    public void setFleaSpawned(boolean fleaSpawned) {
        this.fleaSpawned = fleaSpawned;
    }

    public boolean isFleaAttacked() {
        return fleaAttacked;
    }

    public void setFleaAttacked(boolean fleaAttacked) {
        this.fleaAttacked = fleaAttacked;
    }

    public boolean isDefenderAttacked() {
        return defenderAttacked;
    }

    public void setDefenderAttacked(boolean defenderAttacked) {
        this.defenderAttacked = defenderAttacked;
    }

    public boolean isFleaKilled() {
        return fleaKilled;
    }

    public void setFleaKilled(boolean fleaKilled) {
        this.fleaKilled = fleaKilled;
    }

    public boolean isDefenderDied() {
        return defenderDied;
    }

    public void setDefenderDied(boolean defenderDied) {
        this.defenderDied = defenderDied;
    }

    public boolean isHealed() {
        return healed;
    }

    public void setHealed(boolean healed) {
        this.healed = healed;
    }

    public boolean isDefended() {
        return defended;
    }

    public void setDefended(boolean defended) {
        this.defended = defended;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public void setGameFinished(boolean gameFinished) {
        this.gameFinished = gameFinished;
    }
}
