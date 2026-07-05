package game;

import model.Defender;
import model.Flea;

import java.util.Random;

public class GameEngine {
    private Defender defender;
    private Flea flea;
    private final Random random;
    private int currentTime;
    private final int maxTime;
    private final int fleaSpawnInterval;
    private int nextFleaSpawnTime;
    private int defeatedFleaCount;
    private boolean canTrain;
    private boolean defending;
    private boolean finished;
    private boolean win;

    public GameEngine() {
        this.maxTime = 300;
        this.fleaSpawnInterval = 10;
        this.random = new Random();
        reset();
    }

    public void reset() {
        defender = new Defender();
        flea = null;
        currentTime = 0;
        nextFleaSpawnTime = fleaSpawnInterval;
        defeatedFleaCount = 0;
        canTrain = true;
        defending = false;
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
        return maxTime;
    }

    public int getFleaSpawnInterval() {
        return fleaSpawnInterval;
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

    public boolean isDefending() {
        return defending;
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

    public String getFleaStatusText() {
        if (!hasActiveFlea()) {
            return "Tidak ada Flea aktif";
        }

        return flea.getName() + ": " + flea.getHp() + " / " + flea.getMaxHp();
    }

    public String tickOneSecond() {
        if (finished) {
            return "";
        }

        StringBuilder log = new StringBuilder();

        currentTime++;

        if (currentTime == nextFleaSpawnTime) {
            spawnNewFlea(log);
            nextFleaSpawnTime += fleaSpawnInterval;
        }

        if (hasActiveFlea()) {
            int damage = flea.rollAttackDamage();

            if (defending) {
                int reducedDamage = Math.max(1, damage / 2);
                defender.takeDamage(reducedDamage);
                log.append("[DEFEND] Detik ke-").append(currentTime).append(": Defender menahan serangan. Damage berkurang dari ").append(damage).append(" menjadi ").append(reducedDamage).append(".\n");
            } else {
                defender.takeDamage(damage);
                log.append("[DAMAGE] Detik ke-").append(currentTime).append(": ").append(flea.getName()).append(" menyerang Defender sebesar ").append(damage).append(" damage.\n");
            }

            if (!defender.isAlive()) {
                log.append("[DEAD] HP Defender mencapai 0.\n");
            }
        }

        defending = false;

        updateGameResult();

        if (finished) {
            log.append(getSummary());
        }

        return log.toString();
    }

    private void spawnNewFlea(StringBuilder log) {
        if (hasActiveFlea()) {
            log.append("[SPAWN] Detik ke-").append(currentTime).append(": ").append(flea.getName()).append(" kabur dan digantikan Flea baru.\n");
        }

        flea = Flea.createRandom(random);

        log.append("[SPAWN] Detik ke-").append(currentTime).append(": ").append(flea.getName()).append(" muncul. HP ").append(flea.getMaxHp()).append(", damage ").append(flea.getMinDamage()).append("-").append(flea.getMaxDamage()).append(", reward ").append(flea.getRewardPoint()).append(" RP.\n");
    }

    public String attackFlea() {
        if (finished) {
            return "Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.";
        }

        if (!hasActiveFlea()) {
            canTrain = true;
            defending = false;
            return "[COMBAT] Tidak ada Flea aktif untuk diserang. Tunggu Flea muncul pada detik ke-" + nextFleaSpawnTime + ".\n";
        }

        StringBuilder log = new StringBuilder();

        int damage = defender.attack(flea);

        log.append("[COMBAT] Defender menyerang ").append(flea.getName()).append(" sebesar ").append(damage).append(" damage.\n");
        log.append("[RISIKO] Defender terkena self-damage sebesar ").append(defender.getLastSelfDamage()).append(" damage.\n");

        if (!flea.isAlive()) {
            int reward = flea.getRewardPoint();
            defeatedFleaCount++;
            defender.addResourcePoint(reward);

            log.append("[KILLED] ").append(flea.getName()).append(" berhasil dikalahkan! +").append(reward).append(" RP didapatkan.\n");

            flea = null;
        }

        canTrain = true;
        defending = false;

        updateGameResult();

        if (finished) {
            log.append(getSummary());
        }

        return log.toString();
    }

    public String trainDefender() {
        if (finished) {
            return "Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.";
        }

        if (!canTrain) {
            return "[!] Kamu lelah. Lakukan aksi lain dulu sebelum latihan lagi.\n";
        }

        StringBuilder log = new StringBuilder();

        int healAmount = defender.train();

        log.append("[TRAIN] Defender melakukan latihan fisik.\n");

        if (healAmount > 0) {
            log.append("[HEAL] HP Defender pulih sebesar ").append(healAmount).append(".\n");
        } else {
            log.append("[INFO] HP Defender sudah penuh.\n");
        }

        canTrain = false;
        defending = false;

        updateGameResult();

        return log.toString();
    }

    public String buyVitamin() {
        if (finished) {
            return "Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.";
        }

        StringBuilder log = new StringBuilder();

        int result = defender.buyVitamin();

        if (result == -2) {
            log.append("[SHOP] HP kamu sudah penuh. Vitamin tidak digunakan.\n");
        } else if (result == -1) {
            log.append("[SHOP] RP tidak cukup. Butuh ").append(defender.getVitaminCost()).append(" RP.\n");
        } else {
            log.append("[SHOP] Vitamin berhasil dibeli dan diminum.\n");
            log.append("[HEAL] HP Defender pulih sebesar ").append(result).append(".\n");
        }

        defending = false;

        updateGameResult();

        return log.toString();
    }

    public String skipTime() {
        if (finished) {
            return "Game sudah selesai.\nTekan tombol Restart untuk bermain lagi.";
        }

        canTrain = true;
        defending = true;

        return "[DEFEND] Defender memasang posisi bertahan. Serangan Flea berikutnya akan dikurangi.\n";
    }

    private void updateGameResult() {
        if (!defender.isAlive()) {
            finished = true;
            win = false;
            return;
        }

        if (currentTime >= maxTime) {
            finished = true;
            win = true;
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
