package gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.GridLayout;

public class GameControlPanel extends JPanel {
    private final JButton startStopButton;
    private final JButton attackButton;
    private final JButton defendButton;
    private final JButton trainButton;
    private final JButton vitaminButton;
    private final JButton skipTimeButton;
    private final JButton continueButton;
    private final JButton restartButton;
    private final JToggleButton soundButton;
    private final JButton exitButton;

    public GameControlPanel(
            Runnable startStopAction,
            Runnable attackAction,
            Runnable trainAction,
            Runnable vitaminAction,
            Runnable defendAction,
            Runnable exitAction,
            Runnable skipTimeAction,
            Runnable continueAction,
            Runnable restartAction,
            Runnable soundToggleAction
    ) {
        startStopButton = new JButton("Start Game");
        attackButton = new JButton("Serang Flea");
        defendButton = new JButton("Bertahan");
        trainButton = new JButton("Latihan");
        vitaminButton = new JButton("Beli Vitamin");
        skipTimeButton = new JButton("Lewati 10 Detik");
        continueButton = new JButton("Lanjutkan");
        restartButton = new JButton("Restart Game");
        soundButton = new JToggleButton("Suara: ON");
        exitButton = new JButton("Keluar");

        soundButton.setSelected(true);

        startStopButton.addActionListener(event -> startStopAction.run());
        attackButton.addActionListener(event -> attackAction.run());
        defendButton.addActionListener(event -> defendAction.run());
        trainButton.addActionListener(event -> trainAction.run());
        vitaminButton.addActionListener(event -> vitaminAction.run());
        skipTimeButton.addActionListener(event -> skipTimeAction.run());
        continueButton.addActionListener(event -> continueAction.run());
        restartButton.addActionListener(event -> restartAction.run());
        soundButton.addActionListener(event -> {
            updateSoundButtonText();
            soundToggleAction.run();
        });
        exitButton.addActionListener(event -> exitAction.run());

        buildLayout();
    }

    private void buildLayout() {
        setLayout(new GridLayout(2, 5, 8, 8));

        // Baris pertama: kontrol utama permainan.
        add(startStopButton);
        add(attackButton);
        add(defendButton);
        add(trainButton);
        add(vitaminButton);

        // Baris kedua: kontrol pendukung dan aplikasi.
        add(skipTimeButton);
        add(continueButton);
        add(restartButton);
        add(soundButton);
        add(exitButton);
    }

    public void showStartMode() {
        updateStartStopButton(false, true);

        attackButton.setEnabled(false);
        defendButton.setEnabled(false);
        trainButton.setEnabled(false);
        vitaminButton.setEnabled(false);
        skipTimeButton.setEnabled(false);
        continueButton.setEnabled(false);
        restartButton.setEnabled(false);
        soundButton.setEnabled(true);
        exitButton.setEnabled(true);
    }

    public void updateButtons(
            boolean gameRunning,
            boolean active,
            boolean canAttack,
            boolean canTrain,
            boolean canUseVitamin,
            boolean canDefend,
            boolean showContinue,
            boolean gameFinished
    ) {
        updateStartStopButton(gameRunning, !gameFinished);

        attackButton.setEnabled(active && canAttack);
        defendButton.setEnabled(active && canDefend);
        trainButton.setEnabled(active && canTrain);
        vitaminButton.setEnabled(active && canUseVitamin);
        skipTimeButton.setEnabled(active);
        restartButton.setEnabled(true);
        soundButton.setEnabled(true);
        exitButton.setEnabled(true);

        if (showContinue && gameRunning && !gameFinished) {
            continueButton.setEnabled(true);

            attackButton.setEnabled(false);
            defendButton.setEnabled(false);
            trainButton.setEnabled(false);
            vitaminButton.setEnabled(false);
            skipTimeButton.setEnabled(false);
        } else {
            continueButton.setEnabled(false);
        }
    }

    private void updateStartStopButton(boolean gameRunning, boolean enabled) {
        startStopButton.setText(gameRunning ? "Stop Game" : "Start Game");
        startStopButton.setEnabled(enabled);
        startStopButton.setVisible(true);
    }

    public boolean isSoundEnabled() {
        return soundButton.isSelected();
    }

    private void updateSoundButtonText() {
        soundButton.setText(soundButton.isSelected() ? "Suara: ON" : "Suara: OFF");
    }
}