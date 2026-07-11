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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private final GameEngine gameEngine;
    private final BattlePanel battlePanel;
    private final GameStatusPanel statusPanel;
    private final GameLogPanel logPanel;
    private final GameControlPanel controlPanel;
    private final Timer gameTimer;
    private boolean animationLocked;
    private BattleAnimation forcedActionAnimation;
    private boolean gameStarted;
    private boolean waitingForContinue;

    public GameFrame() {
        gameEngine = new GameEngine();
        battlePanel = new BattlePanel();
        statusPanel = new GameStatusPanel(gameEngine.getMaxTime(), gameEngine.getDefender().getMaxHp());
        logPanel = new GameLogPanel();

        controlPanel = new GameControlPanel(
                this::startGame,
                this::attackFlea,
                this::trainDefender,
                this::buyVitamin,
                this::defend,
                this::exitGame,
                this::skipTime,
                this::continueGame,
                this::restartGame,
                this::toggleSound
        );

        gameTimer = new Timer(1000, event -> runGameTick());
        animationLocked = false;
        forcedActionAnimation = null;
        gameStarted = false;
        waitingForContinue = false;

        battlePanel.setAnimationFinishedListener(this::handleAnimationFinished);

        configureFrame();
        buildLayout();

        logPanel.appendLog("THE FLEA DEFENDER\nTekan tombol 'Start Game' untuk memulai permainan.\n");

        updateView();
        controlPanel.showStartMode();
    }

    private void configureFrame() {
        setTitle("The Flea Defender GUI");
        setSize(1020, 790);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                battlePanel.disposeAudio();
            }
        });
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

    private void startGame() {
        if (gameStarted) {
            return;
        }

        gameStarted = true;
        waitingForContinue = false;

        logPanel.clearLog();
        logPanel.appendLog("Game dimulai!\nFlea pertama muncul pada detik ke-10.\nFlea baru akan muncul setiap 10 detik.\nJika Flea aktif, dia menyerang Defender setiap detik.\nSetiap Flea punya HP, damage, dan reward yang berbeda.\nLatihan memiliki cooldown 10 detik.\nAnimasi vitamin hanya muncul jika RP cukup dan vitamin benar-benar berhasil digunakan.\n");

        controlPanel.showGameMode();
        updateView();
        gameTimer.start();
    }

    private void runGameTick() {
        if (animationLocked || gameEngine.isFinished() || waitingForContinue) {
            return;
        }

        forcedActionAnimation = null;

        GameActionResult result = gameEngine.tickOneSecond();

        if (result.hasMessage() || result.hasAnimationEvent()) {
            setActionLocked(true);
            processResult(result);
            return;
        }

        updateView();
    }

    private void attackFlea() {
        if (animationLocked || !gameStarted || waitingForContinue) {
            return;
        }

        forcedActionAnimation = null;
        setActionLocked(true);
        processResult(gameEngine.attackFlea());
    }

    private void trainDefender() {
        if (animationLocked || !gameStarted || waitingForContinue) {
            return;
        }

        forcedActionAnimation = BattleAnimation.DEFENDER_TRAIN;
        setActionLocked(true);
        processResult(gameEngine.trainDefender());
    }

    private void buyVitamin() {
        if (animationLocked || !gameStarted || waitingForContinue) {
            return;
        }

        forcedActionAnimation = BattleAnimation.DEFENDER_VITAMIN;
        setActionLocked(true);
        processResult(gameEngine.buyVitamin());
    }

    private void defend() {
        if (animationLocked || !gameStarted || waitingForContinue) {
            return;
        }

        forcedActionAnimation = BattleAnimation.DEFENDER_DEFEND;
        setActionLocked(true);
        processResult(gameEngine.defend());
    }

    private void exitGame() {
        battlePanel.disposeAudio();
        System.exit(0);
    }

    private void skipTime() {
        if (animationLocked || !gameStarted || waitingForContinue) {
            return;
        }

        if (gameEngine.isFinished()) {
            return;
        }

        forcedActionAnimation = null;
        setActionLocked(true);

        GameActionResult result = gameEngine.skipTime();
        processResult(result);
    }

    private void continueGame() {
        if (!waitingForContinue) {
            return;
        }

        waitingForContinue = false;

        logPanel.appendLog("[INFO] Permainan dilanjutkan.\n");

        updateView();
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

        boolean fleaWasKilled = result.isFleaKilled();

        forcedActionAnimation = null;

        if (!animationStarted) {
            setActionLocked(false);

            if (fleaWasKilled && !gameEngine.isFinished() && gameEngine.wasFleaKilledEarly()) {
                waitingForContinue = true;
                logPanel.appendLog("[INFO] Flea berhasil dikalahkan! Tekan 'Lanjutkan' untuk melanjutkan permainan.\n");
                updateView();
            }
        }
    }

    private boolean playResultAnimations(GameActionResult result) {
        boolean animationStarted = false;

        if (forcedActionAnimation == BattleAnimation.DEFENDER_TRAIN && shouldPlayTrainingAnimation(result)) {
            battlePanel.playDefenderTrain();
            animationStarted = true;
        }

        if (forcedActionAnimation == BattleAnimation.DEFENDER_VITAMIN && shouldPlayVitaminAnimation(result)) {
            battlePanel.playDefenderVitamin();
            animationStarted = true;
        }

        if (forcedActionAnimation == BattleAnimation.DEFENDER_DEFEND && shouldPlayDefendAnimation(result)) {
            battlePanel.playDefenderDefend();
            animationStarted = true;
        }

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

        if (result.isGuardPrepared() && forcedActionAnimation != BattleAnimation.DEFENDER_DEFEND) {
            battlePanel.playDefenderDefend();
            animationStarted = true;
        }

        if (result.isGuardBlocked() && forcedActionAnimation != BattleAnimation.DEFENDER_DEFEND) {
            battlePanel.playDefenderDefend();
            animationStarted = true;
        }

        if (result.isFleaAttacked()) {
            battlePanel.setFleaVisible(true);
            battlePanel.playFleaAttack();
            animationStarted = true;
        }

        if (result.isHealed() && forcedActionAnimation != BattleAnimation.DEFENDER_TRAIN && forcedActionAnimation != BattleAnimation.DEFENDER_VITAMIN) {
            battlePanel.playHeal();
            animationStarted = true;
        }

        if (result.isDefended() && forcedActionAnimation != BattleAnimation.DEFENDER_DEFEND && !result.isGuardPrepared() && !result.isGuardBlocked()) {
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
            battlePanel.playGameOver();
            animationStarted = true;
        }

        if (result.isGameFinished() && !result.isDefenderDied()) {
            battlePanel.playVictory();
            animationStarted = true;
        }

        return animationStarted;
    }

    private boolean shouldPlayTrainingAnimation(GameActionResult result) {
        String message = result.getMessage();

        if (message.contains("Game sudah selesai")) {
            return false;
        }

        if (message.contains("cooldown")) {
            return false;
        }

        return message.contains("[TRAIN] Defender melakukan latihan");
    }

    private boolean shouldPlayVitaminAnimation(GameActionResult result) {
        return result.isVitaminUsed();
    }

    private boolean shouldPlayDefendAnimation(GameActionResult result) {
        String message = result.getMessage();

        if (message.contains("Game sudah selesai")) {
            return false;
        }

        if (message.contains("Belum ada Flea aktif")) {
            return false;
        }

        if (message.contains("Guard sudah aktif")) {
            return false;
        }

        return message.contains("[DEFEND] Defender memasang guard");
    }

    private void restartGame() {
        gameEngine.reset();
        battlePanel.clearAnimations();
        battlePanel.setFleaVisible(false);
        battlePanel.setDefenderDead(false);
        battlePanel.setSoundEnabled(controlPanel.isSoundEnabled());
        logPanel.clearLog();

        animationLocked = false;
        forcedActionAnimation = null;
        gameStarted = false;
        waitingForContinue = false;

        logPanel.appendLog("THE FLEA DEFENDER\nTekan tombol 'Start Game' untuk memulai permainan.\n");

        if (gameTimer.isRunning()) {
            gameTimer.stop();
        }

        updateView();
        controlPanel.showStartMode();
    }

    private void handleAnimationFinished() {
        if (!gameEngine.hasActiveFlea()) {
            battlePanel.setFleaVisible(false);
        }

        if (!gameEngine.getDefender().isAlive()) {
            battlePanel.setDefenderDead(true);
        }

        boolean fleaJustKilled = !gameEngine.hasActiveFlea() && gameEngine.wasFleaKilledEarly() && !gameEngine.isFinished();

        setActionLocked(false);

        if (fleaJustKilled) {
            waitingForContinue = true;
            logPanel.appendLog("[INFO] Flea berhasil dikalahkan! Tekan 'Lanjutkan' untuk melanjutkan permainan.\n");
        }

        updateView();
    }

    private void toggleSound() {
        battlePanel.setSoundEnabled(controlPanel.isSoundEnabled());
    }

    private void setActionLocked(boolean locked) {
        animationLocked = locked;
        updateView();
    }

    private void updateView() {
        statusPanel.updateStatus(gameEngine);

        if (!gameStarted) {
            controlPanel.showStartMode();
            return;
        }

        boolean active = !gameEngine.isFinished() && !animationLocked && !waitingForContinue;

        controlPanel.updateButtons(
                active,
                gameEngine.hasActiveFlea(),
                gameEngine.canTrain(),
                gameEngine.canUseVitamin(),
                gameEngine.canDefend(),
                waitingForContinue
        );
    }
}