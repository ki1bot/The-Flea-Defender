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
    private final JProgressBar timeBar;
    private final JProgressBar defenderHpBar;
    private final JProgressBar fleaHpBar;
    private final JTextArea logArea;
    private final JButton attackButton;
    private final JButton trainButton;
    private final JButton vitaminButton;
    private final JButton skipButton;
    private final JButton restartButton;

    public GameFrame() {
        gameEngine = new GameEngine();
        battlePanel = new BattlePanel();

        timeLabel = new JLabel();
        defenderHpLabel = new JLabel();
        defenderRpLabel = new JLabel();
        fleaHpLabel = new JLabel();
        trainingStatusLabel = new JLabel();

        timeBar = new JProgressBar(0, gameEngine.getMaxTime());
        defenderHpBar = new JProgressBar(0, gameEngine.getDefender().getMaxHp());
        fleaHpBar = new JProgressBar(0, gameEngine.getFlea().getMaxHp());

        logArea = new JTextArea();

        attackButton = new JButton("Serang Flea");
        trainButton = new JButton("Latihan");
        vitaminButton = new JButton("Beli Vitamin");
        skipButton = new JButton("Bertahan");
        restartButton = new JButton("Restart Game");

        configureFrame();
        buildLayout();
        registerActions();

        appendLog("THE FLEA DEFENDER\nBertahan hidup selama 300 detik.\nFlea menyerang otomatis setiap 20 detik.\nVitamin membutuhkan 20 RP dan memulihkan 30 HP.\nLatihan tidak bisa dilakukan dua kali berturut-turut.\n");

        updateView();
    }

    private void configureFrame() {
        setTitle("The Flea Defender GUI");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    }

    private void buildLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel titleLabel = new JLabel("THE FLEA DEFENDER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(createStatusPanel(), BorderLayout.NORTH);
        centerPanel.add(battlePanel, BorderLayout.CENTER);
        centerPanel.add(createLogPanel(), BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridLayout(5, 2, 10, 8));
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
        scrollPane.setPreferredSize(new Dimension(820, 180));

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
        attackButton.addActionListener(event -> runAction(gameEngine.attackFlea(), "defender_attack"));
        trainButton.addActionListener(event -> runAction(gameEngine.trainDefender(), "heal"));
        vitaminButton.addActionListener(event -> runAction(gameEngine.buyVitamin(), "heal"));
        skipButton.addActionListener(event -> runAction(gameEngine.skipTime(), "defender_defend"));
        restartButton.addActionListener(event -> restartGame());
    }

    private void runAction(String message, String animationType) {
        playPrimaryAnimation(animationType, message);
        playAutomaticAnimation(message);

        appendLog(message);

        if (gameEngine.isFinished()) {
            appendLog(gameEngine.getSummary());
        }

        updateView();
    }

    private void playPrimaryAnimation(String animationType, String message) {
        if ("defender_attack".equals(animationType)) {
            battlePanel.playDefenderAttack();
            return;
        }

        if ("defender_defend".equals(animationType)) {
            battlePanel.playDefenderDefend();
            return;
        }

        if ("heal".equals(animationType)) {
            if (message.contains("pulih sebesar")) {
                battlePanel.playHeal();
            } else {
                battlePanel.playDefenderDefend();
            }
        }
    }

    private void playAutomaticAnimation(String message) {
        if (message.contains("Flea berhasil dikalahkan")) {
            battlePanel.playFleaDefend();
        }

        if (message.contains("Flea menyerang otomatis")) {
            battlePanel.playFleaAttack();
        }

        if (message.contains("HP Defender mencapai 0")) {
            battlePanel.playFleaAttack();
        }
    }

    private void restartGame() {
        gameEngine.reset();
        battlePanel.clearAnimations();
        logArea.setText("");

        appendLog("Game baru dimulai. Bertahan hidup sampai 300 detik.\n");

        updateView();
    }

    private void updateView() {
        timeLabel.setText("Waktu: " + gameEngine.getCurrentTime() + " / " + gameEngine.getMaxTime() + " detik");
        defenderHpLabel.setText("HP Defender: " + gameEngine.getDefender().getHp() + " / " + gameEngine.getDefender().getMaxHp());
        defenderRpLabel.setText("RP Defender: " + gameEngine.getDefender().getResourcePoint());
        fleaHpLabel.setText("HP Flea: " + gameEngine.getFlea().getHp() + " / " + gameEngine.getFlea().getMaxHp());
        trainingStatusLabel.setText("Status Latihan: " + gameEngine.getTrainingStatus());

        timeBar.setValue(gameEngine.getCurrentTime());
        timeBar.setString(gameEngine.getCurrentTime() + " / " + gameEngine.getMaxTime());

        defenderHpBar.setValue(gameEngine.getDefender().getHp());
        defenderHpBar.setString(gameEngine.getDefender().getHp() + " / " + gameEngine.getDefender().getMaxHp());

        fleaHpBar.setValue(gameEngine.getFlea().getHp());
        fleaHpBar.setString(gameEngine.getFlea().getHp() + " / " + gameEngine.getFlea().getMaxHp());

        boolean active = !gameEngine.isFinished();

        attackButton.setEnabled(active);
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
