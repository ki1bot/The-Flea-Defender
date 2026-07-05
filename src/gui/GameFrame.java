package gui;

import game.GameEngine;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class GameFrame extends JFrame {
    private final GameEngine gameEngine;
    private final BattlePanel battlePanel;
    private final JLabel timeLabel;
    private final JLabel defenderHpLabel;
    private final JLabel defenderRpLabel;
    private final JLabel fleaHpLabel;
    private final JLabel trainingStatusLabel;
    private final JLabel spawnInfoLabel;
    private final JProgressBar timeBar;
    private final JProgressBar defenderHpBar;
    private final JProgressBar fleaHpBar;
    private final JTextArea logArea;
    private final JButton attackButton;
    private final JButton trainButton;
    private final JButton vitaminButton;
    private final JButton skipButton;
    private final JButton restartButton;
    private final Timer gameTimer;

    public GameFrame() {
        gameEngine = new GameEngine();
        battlePanel = new BattlePanel();

        timeLabel = new JLabel();
        defenderHpLabel = new JLabel();
        defenderRpLabel = new JLabel();
        fleaHpLabel = new JLabel();
        trainingStatusLabel = new JLabel();
        spawnInfoLabel = new JLabel();

        timeBar = new JProgressBar(0, gameEngine.getMaxTime());
        defenderHpBar = new JProgressBar(0, gameEngine.getDefender().getMaxHp());
        fleaHpBar = new JProgressBar(0, 1);

        logArea = new JTextArea();

        attackButton = new JButton("Serang Flea");
        trainButton = new JButton("Latihan");
        vitaminButton = new JButton("Beli Vitamin");
        skipButton = new JButton("Bertahan");
        restartButton = new JButton("Restart Game");

        gameTimer = new Timer(1000, event -> runGameTick());

        configureFrame();
        buildLayout();
        registerActions();

        appendLog("THE FLEA DEFENDER\nFlea pertama muncul pada detik ke-10.\nFlea baru akan muncul setiap 10 detik.\nJika Flea aktif, dia menyerang Defender setiap detik.\nSetiap Flea punya HP, damage, dan reward yang berbeda.\nDefender harus menyerang untuk membunuh Flea sebelum HP habis.\n");

        updateView();
        gameTimer.start();
    }

    private void configureFrame() {
        setTitle("The Flea Defender GUI");
        setSize(960, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void buildLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel titleLabel = new JLabel("THE FLEA DEFENDER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(createStatusPanel(), BorderLayout.NORTH);
        centerPanel.add(battlePanel, BorderLayout.CENTER);
        centerPanel.add(createLogPanel(), BorderLayout.SOUTH);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(6, 2, 10, 8));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status Game"));

        statusPanel.add(timeLabel);
        statusPanel.add(timeBar);

        statusPanel.add(defenderHpLabel);
        statusPanel.add(defenderHpBar);

        statusPanel.add(defenderRpLabel);
        statusPanel.add(new JLabel("Vitamin: 20 RP / +30 HP"));

        statusPanel.add(fleaHpLabel);
        statusPanel.add(fleaHpBar);

        statusPanel.add(trainingStatusLabel);
        statusPanel.add(spawnInfoLabel);

        statusPanel.add(new JLabel("Flea Dikalahkan: 0"));
        statusPanel.add(new JLabel("Target: Bertahan sampai 300 detik"));

        timeBar.setStringPainted(true);
        defenderHpBar.setStringPainted(true);
        fleaHpBar.setStringPainted(true);

        return statusPanel;
    }

    private JScrollPane createLogPanel() {
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Log Permainan"));
        scrollPane.setPreferredSize(new Dimension(900, 130));

        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 8, 8));

        buttonPanel.add(attackButton);
        buttonPanel.add(trainButton);
        buttonPanel.add(vitaminButton);
        buttonPanel.add(skipButton);
        buttonPanel.add(restartButton);

        return buttonPanel;
    }

    private void registerActions() {
        attackButton.addActionListener(event -> attackFlea());
        trainButton.addActionListener(event -> trainDefender());
        vitaminButton.addActionListener(event -> buyVitamin());
        skipButton.addActionListener(event -> defend());
        restartButton.addActionListener(event -> restartGame());
    }

    private void runGameTick() {
        String message = gameEngine.tickOneSecond();

        if (!message.isEmpty()) {
            if (message.contains("[SPAWN]")) {
                battlePanel.setFleaVisible(gameEngine.hasActiveFlea());
                battlePanel.playFleaDefend();
            }

            if (message.contains("[DAMAGE]") || message.contains("[DEFEND] Detik")) {
                battlePanel.setFleaVisible(true);
                battlePanel.playFleaAttack();
            }

            appendLog(message);
        }

        updateView();

        if (gameEngine.isFinished()) {
            gameTimer.stop();
        }
    }

    private void attackFlea() {
        String message = gameEngine.attackFlea();

        if (message.contains("[COMBAT] Defender menyerang")) {
            battlePanel.setFleaVisible(true);
            battlePanel.playDefenderAttack();
        }

        if (message.contains("[KILLED]")) {
            battlePanel.playFleaDefend();
            scheduleFleaVisibilityRefresh();
        }

        appendLog(message);
        updateView();

        if (gameEngine.isFinished()) {
            gameTimer.stop();
        }
    }

    private void trainDefender() {
        String message = gameEngine.trainDefender();

        if (message.contains("[HEAL]")) {
            battlePanel.playHeal();
        } else {
            battlePanel.playDefenderDefend();
        }

        appendLog(message);
        updateView();
    }

    private void buyVitamin() {
        String message = gameEngine.buyVitamin();

        if (message.contains("[HEAL]")) {
            battlePanel.playHeal();
        } else {
            battlePanel.playDefenderDefend();
        }

        appendLog(message);
        updateView();
    }

    private void defend() {
        String message = gameEngine.skipTime();

        battlePanel.playDefenderDefend();

        appendLog(message);
        updateView();
    }

    private void scheduleFleaVisibilityRefresh() {
        Timer hideTimer = new Timer(900, event -> {
            battlePanel.setFleaVisible(gameEngine.hasActiveFlea());
            battlePanel.repaint();
        });

        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private void restartGame() {
        gameEngine.reset();
        battlePanel.clearAnimations();
        battlePanel.setFleaVisible(false);
        logArea.setText("");

        appendLog("Game baru dimulai.\nFlea pertama akan muncul pada detik ke-10.\n");

        updateView();

        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
    }

    private void updateView() {
        timeLabel.setText("Waktu: " + gameEngine.getCurrentTime() + " / " + gameEngine.getMaxTime() + " detik");
        defenderHpLabel.setText("HP Defender: " + gameEngine.getDefender().getHp() + " / " + gameEngine.getDefender().getMaxHp());
        defenderRpLabel.setText("RP Defender: " + gameEngine.getDefender().getResourcePoint());
        fleaHpLabel.setText("HP Flea: " + gameEngine.getFleaStatusText());
        trainingStatusLabel.setText("Status Latihan: " + gameEngine.getTrainingStatus());

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

        boolean active = !gameEngine.isFinished();

        attackButton.setEnabled(active && gameEngine.hasActiveFlea());
        trainButton.setEnabled(active && gameEngine.canTrain());
        vitaminButton.setEnabled(active);
        skipButton.setEnabled(active);
    }

    private void appendLog(String message) {
        logArea.append(message);

        if (!message.endsWith("\n")) {
            logArea.append("\n");
        }

        logArea.append("\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
