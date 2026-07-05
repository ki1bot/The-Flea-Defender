package gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.GridLayout;

public class GameControlPanel extends JPanel {
    private final JButton attackButton;
    private final JButton trainButton;
    private final JButton vitaminButton;
    private final JButton defendButton;
    private final JButton restartButton;
    private final JToggleButton soundButton;

    public GameControlPanel(Runnable attackAction, Runnable trainAction, Runnable vitaminAction, Runnable defendAction, Runnable restartAction, Runnable soundToggleAction) {
        attackButton = new JButton("Serang Flea");
        trainButton = new JButton("Latihan");
        vitaminButton = new JButton("Beli Vitamin");
        defendButton = new JButton("Bertahan");
        restartButton = new JButton("Restart Game");
        soundButton = new JToggleButton("Suara: ON");

        soundButton.setSelected(true);

        attackButton.addActionListener(event -> attackAction.run());
        trainButton.addActionListener(event -> trainAction.run());
        vitaminButton.addActionListener(event -> vitaminAction.run());
        defendButton.addActionListener(event -> defendAction.run());
        restartButton.addActionListener(event -> restartAction.run());
        soundButton.addActionListener(event -> {
            updateSoundButtonText();
            soundToggleAction.run();
        });

        buildLayout();
    }

    private void buildLayout() {
        setLayout(new GridLayout(1, 6, 8, 8));

        add(attackButton);
        add(trainButton);
        add(vitaminButton);
        add(defendButton);
        add(restartButton);
        add(soundButton);
    }

    public void updateButtons(boolean active, boolean canAttack, boolean canTrain, boolean canUseVitamin, boolean canDefend) {
        attackButton.setEnabled(active && canAttack);
        trainButton.setEnabled(active && canTrain);
        vitaminButton.setEnabled(active && canUseVitamin);
        defendButton.setEnabled(active && canDefend);
        restartButton.setEnabled(true);
        soundButton.setEnabled(true);
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
