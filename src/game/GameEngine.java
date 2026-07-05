package game;

import model.Defender;
import model.Flea;

import java.util.Random;

public class GameEngine {
    private Defender defender;
    private Flea flea;
    private final Random random;
    private final FleaFactory fleaFactory;
    private int currentTime;
    private int nextFleaSpawnTime;
    private int defeatedFleaCount;
    private boolean canTrain;
    private boolean guarding;
    private boolean finished;
    private boolean win;

    public GameEngine() {
        random = new Random();
        fleaFactory = new FleaFactory(random);
        reset();
    }

    public void reset() {
        defender = new Defender();
        flea = null;
        currentTime = 0;
        nextFleaSpawnTime = GameBalance.FLEA_SPAWN_INTERVAL;
        defeatedFleaCount = 0;
        canTrain = true;
        guarding = false;
        finished = false;
        win = false;
    }

    public Defender getDefender() {
        return defender;
    }

    public Flea getFlea() {
        return flea;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getMaxTime() {
        return GameBalance.MAX_TIME;
    }

    public int getNextFleaSpawnTime() {
        return nextFleaSpawnTime;
    }

    public int getDefeatedFleaCount() {
        return defeatedFleaCount;
    }

    public boolean canTrain() {
        return canTrain;
    }

    public boolean isGuarding() {
        return guarding;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWin() {
        return win;
    }

    public boolean hasActiveFlea() {
        return flea != null && flea.isAlive();
    }

    public String getTrainingStatus() {
        return canTrain ? "Siap" : "Cooldown";
    }

    public String getGuardStatusText() {
        return guarding ? "Aktif sampai serangan Flea berikutnya" : "Tidak aktif";
    }

    public String getFleaStatusText() {
        if (!hasActiveFlea()) {
            return "Tidak ada Flea aktif";
        }

        return flea.getName() + ": " + flea.getHp() + " / " + flea.getMaxHp();
    }

    public GameActionResult tickOneSecond() {
        GameActionResult result = new GameActionResult();

        if (finished) {
            return result;
        }

        currentTime++;

        if (currentTime == nextFleaSpawnTime) {
            spawnNewFlea(result);
            nextFleaSpawnTime += GameBalance.FLEA_SPAWN_INTERVAL;
        }

        if (hasActiveFlea()) {
            applyFleaAttack(result);
        }

        updateGameResult(result);

        return result;
    }

    private void spawnNewFlea(GameActionResult result) {
        if (hasActiveFlea()) {
            result.addLog("[SPAWN] Detik ke-" + currentTime + ": " + flea.getName() + " kabur dan digantikan Flea baru.\n");
        }

        flea = fleaFactory.createRandomFlea();
        result.setFleaSpawned(true);

        result.addLog("[SPAWN] Detik ke-" + currentTime + ": " + flea.getName() + " muncul. HP " + flea.getMaxHp() + ", damage " + flea.getMinDamage() + "-" + flea.getMaxDamage() + ", reward " + flea.getRewardPoint() + " RP.\n");
    }

    private void applyFleaAttack(GameActionResult result) {
        int damage = flea.rollAttackDamage();
        result.setFleaAttacked(true);

        if (guarding) {
            int reducedDamage = Math.max(GameBalance.GUARD_MIN_DAMAGE, damage / GameBalance.GUARD_DAMAGE_DIVIDER);
            defender.takeDamage(reducedDamage);

            result.setDefended(true);
            result.setGuardBlocked(true);
            result.addLog("[GUARD] Detik ke-" + currentTime + ": Defender berhasil bertahan dari serangan " + flea.getName() + ". Damage " + damage + " dikurangi menjadi " + reducedDamage + ".\n");

            guarding = false;
        } else {
            defender.takeDamage(damage);
            result.addLog("[DAMAGE] Detik ke-" + currentTime + ": " + flea.getName() + " menyerang Defender sebesar " + damage + " damage.\n");
        }

        if (!defender.isAlive()) {
            result.setDefenderDied(true);
            result.addLog("[DEAD] Defender mati karena HP mencapai 0.\n");
        }
    }

    public GameActionResult attackFlea() {
        GameActionResult result = new GameActionResult();

        if (finished) {
            result.addLog("Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.\n");
            return result;
        }

        if (!hasActiveFlea()) {
            canTrain = true;
            result.addLog("[COMBAT] Tidak ada Flea aktif untuk diserang. Tunggu Flea muncul pada detik ke-" + nextFleaSpawnTime + ".\n");
            return result;
        }

        int damage = defender.attack(flea);
        result.setDefenderAttacked(true);

        result.addLog("[COMBAT] Defender menyerang " + flea.getName() + " sebesar " + damage + " damage.\n");
        result.addLog("[RISIKO] Defender terkena self-damage sebesar " + defender.getLastSelfDamage() + " damage.\n");

        if (!flea.isAlive()) {
            int reward = flea.getRewardPoint();
            defeatedFleaCount++;
            defender.addResourcePoint(reward);

            result.setFleaKilled(true);
            result.addLog("[KILLED] " + flea.getName() + " mati. +" + reward + " RP didapatkan.\n");

            flea = null;
        }

        if (!defender.isAlive()) {
            result.setDefenderDied(true);
            result.addLog("[DEAD] Defender mati karena self-damage.\n");
        }

        canTrain = true;
        updateGameResult(result);

        return result;
    }

    public GameActionResult trainDefender() {
        GameActionResult result = new GameActionResult();

        if (finished) {
            result.addLog("Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.\n");
            return result;
        }

        if (!canTrain) {
            result.addLog("[!] Kamu lelah. Lakukan aksi lain dulu sebelum latihan lagi.\n");
            return result;
        }

        int healAmount = defender.train();

        result.addLog("[TRAIN] Defender melakukan latihan fisik.\n");

        if (healAmount > 0) {
            result.setHealed(true);
            result.addLog("[HEAL] HP Defender pulih sebesar " + healAmount + ".\n");
        } else {
            result.addLog("[INFO] HP Defender sudah penuh.\n");
        }

        canTrain = false;
        updateGameResult(result);

        return result;
    }

    public GameActionResult buyVitamin() {
        GameActionResult result = new GameActionResult();

        if (finished) {
            result.addLog("Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.\n");
            return result;
        }

        int vitaminResult = defender.buyVitamin();

        if (vitaminResult == -2) {
            result.addLog("[SHOP] HP kamu sudah penuh. Vitamin tidak digunakan.\n");
        } else if (vitaminResult == -1) {
            result.addLog("[SHOP] RP tidak cukup. Butuh " + defender.getVitaminCost() + " RP.\n");
        } else {
            result.setVitaminUsed(true);
            result.setHealed(true);
            result.addLog("[SHOP] Vitamin berhasil dibeli dan diminum.\n");
            result.addLog("[HEAL] HP Defender pulih sebesar " + vitaminResult + ".\n");
        }

        updateGameResult(result);

        return result;
    }

    public GameActionResult defend() {
        GameActionResult result = new GameActionResult();

        if (finished) {
            result.addLog("Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.\n");
            return result;
        }

        canTrain = true;
        guarding = true;

        result.setDefended(true);
        result.setGuardPrepared(true);
        result.addLog("[DEFEND] Defender memasang guard. Serangan Flea berikutnya akan dikurangi besar.\n");

        return result;
    }

    public GameActionResult skipTime() {
        return defend();
    }

    private void updateGameResult(GameActionResult result) {
        if (finished) {
            return;
        }

        if (!defender.isAlive()) {
            finished = true;
            win = false;
            result.setGameFinished(true);
            result.addLog(getSummary());
            return;
        }

        if (currentTime >= GameBalance.MAX_TIME) {
            finished = true;
            win = true;
            result.setGameFinished(true);
            result.addLog(getSummary());
        }
    }

    public String getSummary() {
        StringBuilder summary = new StringBuilder();

        summary.append("\n========== RINGKASAN AKHIR GAME ==========\n");

        if (win) {
            summary.append("STATUS AKHIR : BERHASIL BERTAHAN (VICTORY)\n");
        } else {
            summary.append("STATUS AKHIR : DEFENDER MATI (GAME OVER)\n");
        }

        summary.append("Waktu Akhir : ").append(currentTime).append(" detik\n");
        summary.append("Sisa HP : ").append(defender.getHp()).append("\n");
        summary.append("Total RP : ").append(defender.getResourcePoint()).append("\n");
        summary.append("Flea Dikalahkan : ").append(defeatedFleaCount).append("\n");
        summary.append("==========================================\n");

        return summary.toString();
    }
}
