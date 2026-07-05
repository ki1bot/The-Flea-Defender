package gui;

import game.GameActionResult;
import game.GameEngine;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Font;

public class GameFrame extends JFrame {
    private final GameEngine gameEngine;
    private final BattlePanel battlePanel;
    private final GameStatusPanel statusPanel;
    private final GameLogPanel logPanel;
    private final GameControlPanel controlPanel;
    private final Timer gameTimer;
    private boolean animationLocked;

    public GameFrame() {
        gameEngine = new GameEngine();
        battlePanel = new BattlePanel();
        statusPanel = new GameStatusPanel(gameEngine.getMaxTime(), gameEngine.getDefender().getMaxHp());
        logPanel = new GameLogPanel();
        controlPanel = new GameControlPanel(
                this::attackFlea,
                this::trainDefender,
                this::buyVitamin,
                this::defend,
                this::restartGame
        );

        gameTimer = new Timer(1000, event -> runGameTick());
        animationLocked = false;

        battlePanel.setAnimationFinishedListener(this::handleAnimationFinished);

        configureFrame();
        buildLayout();

        logPanel.appendLog("THE FLEA DEFENDER\nFlea pertama muncul pada detik ke-10.\nFlea baru akan muncul setiap 10 detik.\nJika Flea aktif, dia menyerang Defender setiap detik.\nSetiap Flea punya HP, damage, dan reward yang berbeda.\nTombol aksi dikunci selama animasi berjalan supaya input tidak menumpuk.\n");

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
        centerPanel.add(statusPanel, BorderLayout.NORTH);
        centerPanel.add(battlePanel, BorderLayout.CENTER);
        centerPanel.add(logPanel, BorderLayout.SOUTH);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void runGameTick() {
        if (animationLocked || gameEngine.isFinished()) {
            return;
        }

        GameActionResult result = gameEngine.tickOneSecond();

        if (result.hasMessage() || result.hasAnimationEvent()) {
            setActionLocked(true);
            processResult(result);
            return;
        }

        updateView();
    }

    private void attackFlea() {
        if (animationLocked) {
            return;
        }

        setActionLocked(true);
        processResult(gameEngine.attackFlea());
    }

    private void trainDefender() {
        if (animationLocked) {
            return;
        }

        setActionLocked(true);
        processResult(gameEngine.trainDefender());
    }

    private void buyVitamin() {
        if (animationLocked) {
            return;
        }

        setActionLocked(true);
        processResult(gameEngine.buyVitamin());
    }

    private void defend() {
        if (animationLocked) {
            return;
        }

        setActionLocked(true);
        processResult(gameEngine.defend());
    }

    private void processResult(GameActionResult result) {
        if (result.hasMessage()) {
            logPanel.appendLog(result.getMessage());
        }

        boolean animationStarted = playResultAnimations(result);

        updateView();

        if (gameEngine.isFinished()) {
            gameTimer.stop();
        }

        if (!animationStarted) {
            setActionLocked(false);
        }
    }

    private boolean playResultAnimations(GameActionResult result) {
        boolean animationStarted = false;

        if (result.isFleaSpawned()) {
            battlePanel.setFleaVisible(true);
            battlePanel.playFleaEnter();
            animationStarted = true;
        }

        if (result.isDefenderAttacked()) {
            battlePanel.setFleaVisible(true);
            battlePanel.playDefenderAttack();
            animationStarted = true;
        }

        if (result.isFleaAttacked()) {
            battlePanel.setFleaVisible(true);
            battlePanel.playFleaAttack();
            animationStarted = true;
        }

        if (result.isHealed()) {
            battlePanel.playHeal();
            animationStarted = true;
        }

        if (result.isDefended()) {
            battlePanel.playDefenderDefend();
            animationStarted = true;
        }

        if (result.isFleaKilled()) {
            battlePanel.setFleaVisible(true);
            battlePanel.playFleaDeath();
            animationStarted = true;
        }

        if (result.isDefenderDied()) {
            battlePanel.playDefenderDeath();
            animationStarted = true;
        }

        return animationStarted;
    }

    private void restartGame() {
        gameEngine.reset();
        battlePanel.clearAnimations();
        battlePanel.setFleaVisible(false);
        battlePanel.setDefenderDead(false);
        logPanel.clearLog();
        animationLocked = false;

        logPanel.appendLog("Game baru dimulai.\nFlea pertama akan muncul pada detik ke-10.\n");

        updateView();

        if (!gameTimer.isRunning()) {
            gameTimer.start();
        }
    }

    private void handleAnimationFinished() {
        if (!gameEngine.hasActiveFlea()) {
            battlePanel.setFleaVisible(false);
        }

        if (!gameEngine.getDefender().isAlive()) {
            battlePanel.setDefenderDead(true);
        }

        setActionLocked(false);
        updateView();
    }

    private void setActionLocked(boolean locked) {
        animationLocked = locked;
        updateView();
    }

    private void updateView() {
        statusPanel.updateStatus(gameEngine);

        boolean active = !gameEngine.isFinished() && !animationLocked;

        controlPanel.updateButtons(
                active,
                gameEngine.hasActiveFlea(),
                gameEngine.canTrain(),
                true,
                true
        );
    }
}
