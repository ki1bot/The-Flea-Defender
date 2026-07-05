package gui;

import game.GameEngine;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.GridLayout;

public class GameStatusPanel extends JPanel {
    private final JLabel timeLabel;
    private final JLabel defenderHpLabel;
    private final JLabel defenderRpLabel;
    private final JLabel fleaHpLabel;
    private final JLabel trainingStatusLabel;
    private final JLabel spawnInfoLabel;
    private final JLabel defeatedFleaLabel;
    private final JProgressBar timeBar;
    private final JProgressBar defenderHpBar;
    private final JProgressBar fleaHpBar;

    public GameStatusPanel(int maxTime, int defenderMaxHp) {
        timeLabel = new JLabel();
        defenderHpLabel = new JLabel();
        defenderRpLabel = new JLabel();
        fleaHpLabel = new JLabel();
        trainingStatusLabel = new JLabel();
        spawnInfoLabel = new JLabel();
        defeatedFleaLabel = new JLabel();

        timeBar = new JProgressBar(0, maxTime);
        defenderHpBar = new JProgressBar(0, defenderMaxHp);
        fleaHpBar = new JProgressBar(0, 1);

        buildLayout();
    }

    private void buildLayout() {
        setLayout(new GridLayout(6, 2, 10, 8));
        setBorder(BorderFactory.createTitledBorder("Status Game"));

        add(timeLabel);
        add(timeBar);

        add(defenderHpLabel);
        add(defenderHpBar);

        add(defenderRpLabel);
        add(new JLabel("Vitamin: 20 RP / +30 HP"));

        add(fleaHpLabel);
        add(fleaHpBar);

        add(trainingStatusLabel);
        add(spawnInfoLabel);

        add(defeatedFleaLabel);
        add(new JLabel("Target: Bertahan sampai 300 detik"));

        timeBar.setStringPainted(true);
        defenderHpBar.setStringPainted(true);
        fleaHpBar.setStringPainted(true);
    }

    public void updateStatus(GameEngine gameEngine) {
        timeLabel.setText("Waktu: " + gameEngine.getCurrentTime() + " / " + gameEngine.getMaxTime() + " detik");
        defenderHpLabel.setText("HP Defender: " + gameEngine.getDefender().getHp() + " / " + gameEngine.getDefender().getMaxHp());
        defenderRpLabel.setText("RP Defender: " + gameEngine.getDefender().getResourcePoint());
        fleaHpLabel.setText("HP Flea: " + gameEngine.getFleaStatusText());
        trainingStatusLabel.setText("Status Latihan: " + gameEngine.getTrainingStatus());
        defeatedFleaLabel.setText("Flea Dikalahkan: " + gameEngine.getDefeatedFleaCount());

        if (gameEngine.hasActiveFlea()) {
            spawnInfoLabel.setText("Flea aktif menyerang setiap detik");
        } else {
            spawnInfoLabel.setText("Flea berikutnya: detik ke-" + gameEngine.getNextFleaSpawnTime());
        }

        timeBar.setValue(gameEngine.getCurrentTime());
        timeBar.setString(gameEngine.getCurrentTime() + " / " + gameEngine.getMaxTime());

        defenderHpBar.setMaximum(gameEngine.getDefender().getMaxHp());
        defenderHpBar.setValue(gameEngine.getDefender().getHp());
        defenderHpBar.setString(gameEngine.getDefender().getHp() + " / " + gameEngine.getDefender().getMaxHp());

        if (gameEngine.hasActiveFlea()) {
            fleaHpBar.setMaximum(gameEngine.getFlea().getMaxHp());
            fleaHpBar.setValue(gameEngine.getFlea().getHp());
            fleaHpBar.setString(gameEngine.getFlea().getHp() + " / " + gameEngine.getFlea().getMaxHp());
        } else {
            fleaHpBar.setMaximum(1);
            fleaHpBar.setValue(0);
            fleaHpBar.setString("Tidak ada Flea");
        }
    }
}
