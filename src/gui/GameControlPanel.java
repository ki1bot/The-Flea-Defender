package gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.GridLayout;

public class GameControlPanel extends JPanel {
    private final JButton startButton;
    private final JButton attackButton;
    private final JButton trainButton;
    private final JButton vitaminButton;
    private final JButton defendButton;
    private final JButton exitButton;
    private final JButton skipTimeButton;
    private final JButton continueButton;
    private final JButton restartButton;
    private final JToggleButton soundButton;

    public GameControlPanel(Runnable startAction, Runnable attackAction, Runnable trainAction, Runnable vitaminAction, Runnable defendAction, Runnable exitAction, Runnable skipTimeAction, Runnable continueAction, Runnable restartAction, Runnable soundToggleAction) {
        startButton = new JButton("Start Game");
        attackButton = new JButton("Serang Flea");
        trainButton = new JButton("Latihan");
        vitaminButton = new JButton("Beli Vitamin");
        defendButton = new JButton("Bertahan");
        exitButton = new JButton("Keluar");
        skipTimeButton = new JButton("Lewati 10 Detik");
        continueButton = new JButton("Lanjutkan");
        restartButton = new JButton("Restart Game");
        soundButton = new JToggleButton("Suara: ON");

        soundButton.setSelected(true);

        startButton.addActionListener(event -> startAction.run());
        attackButton.addActionListener(event -> attackAction.run());
        trainButton.addActionListener(event -> trainAction.run());
        vitaminButton.addActionListener(event -> vitaminAction.run());
        defendButton.addActionListener(event -> defendAction.run());
        exitButton.addActionListener(event -> exitAction.run());
        skipTimeButton.addActionListener(event -> skipTimeAction.run());
        continueButton.addActionListener(event -> continueAction.run());
        restartButton.addActionListener(event -> restartAction.run());
        soundButton.addActionListener(event -> {
            updateSoundButtonText();
            soundToggleAction.run();
        });

        buildLayout();
    }

    private void buildLayout() {
        setLayout(new GridLayout(2, 5, 8, 8));

        add(startButton);
        add(attackButton);
        add(trainButton);
        add(vitaminButton);
        add(defendButton);
        add(exitButton);
        add(skipTimeButton);
        add(continueButton);
        add(restartButton);
        add(soundButton);
    }

    public void showStartMode() {
        startButton.setEnabled(true);
        startButton.setVisible(true);

        attackButton.setEnabled(false);
        trainButton.setEnabled(false);
        vitaminButton.setEnabled(false);
        defendButton.setEnabled(false);
        exitButton.setEnabled(true);
        skipTimeButton.setEnabled(false);
        continueButton.setEnabled(false);
        restartButton.setEnabled(false);
        soundButton.setEnabled(true);
    }

    public void showGameMode() {
        startButton.setEnabled(false);
        startButton.setVisible(false);
    }

    public void updateButtons(boolean active, boolean canAttack, boolean canTrain, boolean canUseVitamin, boolean canDefend, boolean showContinue) {
        startButton.setEnabled(false);
        startButton.setVisible(false);

        attackButton.setEnabled(active && canAttack);
        trainButton.setEnabled(active && canTrain);
        vitaminButton.setEnabled(active && canUseVitamin);
        defendButton.setEnabled(active && canDefend);
        exitButton.setEnabled(true);
        skipTimeButton.setEnabled(active);
        restartButton.setEnabled(true);
        soundButton.setEnabled(true);

        if (showContinue) {
            continueButton.setEnabled(true);

            attackButton.setEnabled(false);
            trainButton.setEnabled(false);
            vitaminButton.setEnabled(false);
            defendButton.setEnabled(false);
            skipTimeButton.setEnabled(false);
        } else {
            continueButton.setEnabled(false);
        }
    }

    public boolean isSoundEnabled() {
        return soundButton.isSelected();
    }

    private void updateSoundButtonText() {
        if (soundButton.isSelected()) {
            soundButton.setText("Suara: ON");
        } else {
            soundButton.setText("Suara: OFF");
        }
    }
}
