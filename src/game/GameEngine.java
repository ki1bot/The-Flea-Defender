package game;

import model.Defender;
import model.Flea;

public class GameEngine {
    private Defender defender;
    private Flea flea;
    private int currentTime;
    private final int maxTime;
    private final int fleaAttackInterval;
    private int nextFleaAttackTime;
    private boolean canTrain;
    private boolean finished;
    private boolean win;

    public GameEngine() {
        this.maxTime = 300;
        this.fleaAttackInterval = 20;
        reset();
    }

    public void reset() {
        defender = new Defender();
        flea = new Flea();
        currentTime = 0;
        nextFleaAttackTime = fleaAttackInterval;
        canTrain = true;
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

    public boolean canTrain() {
        return canTrain;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWin() {
        return win;
    }

    public String getTrainingStatus() {
        return canTrain ? "Siap" : "Cooldown";
    }

    public String attackFlea() {
        if (finished) {
            return "Game sudah selesai. Tekan tombol Restart untuk bermain lagi.";
        }

        StringBuilder log = new StringBuilder();
        int damage = defender.attack(flea);
        log.append("[COMBAT] Defender menyerang Flea sebesar ").append(damage).append(" damage.\n");
        log.append("[RISIKO] Defender terkena self-damage sebesar ").append(defender.getLastSelfDamage()).append(" damage.\n");

        if (!flea.isAlive()) {
            int reward = 20;
            defender.addResourcePoint(reward);
            log.append("[KILLED] Flea berhasil dikalahkan! +").append(reward).append(" RP didapatkan.\n");
            flea.respawn();
            log.append("[SPAWN] Flea baru telah muncul.\n");
        }

        canTrain = true;
        log.append(advanceTime(10));
        updateGameResult();
        return log.toString();
    }

    public String trainDefender() {
        if (finished) {
            return "Game sudah selesai. Tekan tombol Restart untuk bermain lagi.";
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
        log.append(advanceTime(10));
        updateGameResult();
        return log.toString();
    }

    public String buyVitamin() {
        if (finished) {
            return "Game sudah selesai. Tekan tombol Restart untuk bermain lagi.";
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

        updateGameResult();
        return log.toString();
    }

    public String skipTime() {
        if (finished) {
            return "Game sudah selesai. Tekan tombol Restart untuk bermain lagi.";
        }

        StringBuilder log = new StringBuilder();
        log.append("[WAIT] Defender bersiap siaga dan membiarkan waktu berjalan.\n");
        canTrain = true;
        log.append(advanceTime(10));
        updateGameResult();
        return log.toString();
    }

    private String advanceTime(int seconds) {
        StringBuilder log = new StringBuilder();
        int targetTime = currentTime + seconds;

        if (targetTime > maxTime) {
            targetTime = maxTime;
        }

        while (currentTime < targetTime && defender.isAlive()) {
            currentTime++;

            if (currentTime == nextFleaAttackTime) {
                log.append(checkFleaAttack());
                nextFleaAttackTime += fleaAttackInterval;
            }
        }

        return log.toString();
    }

    private String checkFleaAttack() {
        StringBuilder log = new StringBuilder();
        log.append("[EVENT] Detik ke-").append(currentTime).append(": Flea menyerang otomatis.\n");
        int damage = flea.attack(defender);
        log.append("[DAMAGE] Defender terkena ").append(damage).append(" damage.\n");

        if (!defender.isAlive()) {
            log.append("[DEAD] HP Defender mencapai 0.\n");
        }

        return log.toString();
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
        summary.append("==========================================\n");
        return summary.toString();
    }
}
