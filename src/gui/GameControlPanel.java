package gui;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class GameControlPanel extends JPanel {
    private final JButton attackButton;
    private final JButton trainButton;
    private final JButton vitaminButton;
    private final JButton defendButton;
    private final JButton restartButton;

    public GameControlPanel(Runnable attackAction, Runnable trainAction, Runnable vitaminAction, Runnable defendAction, Runnable restartAction) {
        attackButton = new JButton("Serang Flea");
        trainButton = new JButton("Latihan");
        vitaminButton = new JButton("Beli Vitamin");
        defendButton = new JButton("Bertahan");
        restartButton = new JButton("Restart Game");

        attackButton.addActionListener(event -> attackAction.run());
        trainButton.addActionListener(event -> trainAction.run());
        vitaminButton.addActionListener(event -> vitaminAction.run());
        defendButton.addActionListener(event -> defendAction.run());
        restartButton.addActionListener(event -> restartAction.run());

        buildLayout();
    }

    private void buildLayout() {
        setLayout(new GridLayout(1, 5, 8, 8));

        add(attackButton);
        add(trainButton);
        add(vitaminButton);
        add(defendButton);
        add(restartButton);
    }

    public void updateButtons(boolean active, boolean canAttack, boolean canTrain, boolean canUseVitamin, boolean canDefend) {
        attackButton.setEnabled(active && canAttack);
        trainButton.setEnabled(active && canTrain);
        vitaminButton.setEnabled(active && canUseVitamin);
        defendButton.setEnabled(active && canDefend);
        restartButton.setEnabled(true);
    }
}
