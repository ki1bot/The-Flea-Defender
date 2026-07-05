package gui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

public class GameLogPanel extends JPanel {
    private final JTextArea logArea;

    public GameLogPanel() {
        logArea = new JTextArea();
        buildLayout();
    }

    private void buildLayout() {
        setLayout(new BorderLayout());

        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Log Permainan"));
        scrollPane.setPreferredSize(new Dimension(900, 130));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void appendLog(String message) {
        logArea.append(message);

        if (!message.endsWith("\n")) {
            logArea.append("\n");
        }

        logArea.append("\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clearLog() {
        logArea.setText("");
    }
}
