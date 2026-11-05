import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class GameFrame extends JFrame {
    private static final int HUMAN_COLOR = Board.WHITE;
    private static final int AI_COLOR = Board.BLACK;

    private final Game game;
    private final BoardPanel boardPanel;
    private final JLabel statusLabel;
    private final JLabel piecesLabel;
    private final JComboBox<String> modeBox;
    private final JComboBox<String> difficultyBox;
    private final JButton newButton;
    private final JButton undoButton;

    private boolean aiThinking;
    private boolean gameOverShown;
    private int positionVersion;

    public GameFrame() {
        super("Русские шашки");

        game = new Game();
        boardPanel = new BoardPanel();
        boardPanel.setGame(game);
        boardPanel.setOnMove(this::afterMove);

        statusLabel = new JLabel("Ход белых");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 16f));
        piecesLabel = new JLabel();

        modeBox = new JComboBox<>(new String[]{"Против компьютера", "Два игрока"});
        difficultyBox = new JComboBox<>(new String[]{"Лёгкий", "Средний", "Сложный"});
        difficultyBox.setSelectedIndex(1);

        newButton = new JButton("Новая игра");
        undoButton = new JButton("Отменить ход");

        newButton.addActionListener(e -> newGame());
        undoButton.addActionListener(e -> undoMove());
        modeBox.addActionListener(e -> {
            if (!aiThinking) {
                newGame();
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.add(new JLabel("Режим:"));
        top.add(modeBox);
        top.add(new JLabel("Сложность:"));
        top.add(difficultyBox);
        top.add(newButton);
        top.add(undoButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(piecesLabel, BorderLayout.EAST);

        JLabel hint = new JLabel("Мышь: выбрать шашку и клетку.  Клавиатура: стрелки, Enter, Esc.  N — новая игра, U — отмена.");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12f));

        JPanel south = new JPanel(new BorderLayout());
        south.add(bottom, BorderLayout.NORTH);
        south.add(hint, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        installShortcuts();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(900, screen.width - 40), Math.min(850, screen.height - 80));
        setMinimumSize(new Dimension(640, 640));
        setLocationRelativeTo(null);

        updateStatus();
    }

    private void installShortcuts() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, 0), "newGame");
        getRootPane().getActionMap().put("newGame", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0), "undo");
        getRootPane().getActionMap().put("undo", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                undoMove();
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undoCtrl");
        getRootPane().getActionMap().put("undoCtrl", new javax.swing.AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                undoMove();
            }
        });
    }

    private void newGame() {
        positionVersion++;
        aiThinking = false;
        gameOverShown = false;
        game.newGame();
        boardPanel.setInputEnabled(true);
        boardPanel.clearSelection();
        boardPanel.repaint();
        setControlsEnabled(true);
        updateStatus();

        if (isComputerMode() && game.getTurn() == AI_COLOR) {
            startAiTurn();
        }
    }

    private void undoMove() {
        if (aiThinking || !game.canUndo()) {
            return;
        }

        if (isComputerMode() && game.getTurn() == HUMAN_COLOR) {
            game.undo();
            if (game.canUndo()) {
                game.undo();
            }
        } else {
            game.undo();
        }

        gameOverShown = false;
        boardPanel.clearSelection();
        boardPanel.repaint();
        updateStatus();
    }

    private void afterMove() {
        updateStatus();
        if (game.isGameOver()) {
            showGameOver();
            return;
        }
        if (isComputerMode() && game.getTurn() == AI_COLOR) {
            startAiTurn();
        }
    }

    private void startAiTurn() {
        if (aiThinking || game.isGameOver()) {
            return;
        }

        aiThinking = true;
        final int version = positionVersion;
        final int depth = selectedDepth();
        final Board boardForAi = game.getBoard().copy();
        final int aiColor = game.getTurn();

        boardPanel.setInputEnabled(false);
        setControlsEnabled(false);
        statusLabel.setText("Компьютер думает...");

        SwingWorker<Move, Void> worker = new SwingWorker<Move, Void>() {
            @Override
            protected Move doInBackground() {
                AI ai = new AI(depth);
                return ai.chooseMove(boardForAi, aiColor);
            }

            @Override
            protected void done() {
                if (version != positionVersion) {
                    return;
                }

                try {
                    Move move = get();
                    if (move != null) {
                        game.makeMove(move);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(GameFrame.this,
                            "Ошибка при выборе хода компьютером: " + ex.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

                aiThinking = false;
                boardPanel.setInputEnabled(true);
                setControlsEnabled(true);
                boardPanel.repaint();
                updateStatus();

                if (game.isGameOver()) {
                    showGameOver();
                }
            }
        };
        worker.execute();
    }

    private int selectedDepth() {
        int index = difficultyBox.getSelectedIndex();
        if (index == 0) {
            return 2;
        }
        if (index == 2) {
            return 6;
        }
        return 4;
    }

    private boolean isComputerMode() {
        return modeBox.getSelectedIndex() == 0;
    }

    private void setControlsEnabled(boolean enabled) {
        newButton.setEnabled(enabled);
        modeBox.setEnabled(enabled);
        difficultyBox.setEnabled(enabled);
        undoButton.setEnabled(enabled && game.canUndo());
    }

    private void updateStatus() {
        if (game.isGameOver()) {
            if (game.getWinner() == 0) {
                statusLabel.setText("Ничья");
            } else {
                statusLabel.setText("Победа: " + game.getWinnerName());
            }
        } else {
            String text = "Ход " + game.getTurnName();
            if (isComputerMode() && game.getTurn() == AI_COLOR) {
                text += " (компьютер)";
            }
            statusLabel.setText(text);
        }

        String pieces = "Белые: " + game.getBoard().count(Board.WHITE)
                + "   Чёрные: " + game.getBoard().count(Board.BLACK);
        if (game.getLastMove() != null) {
            pieces += "   Последний ход: " + game.getLastMove();
        }
        piecesLabel.setText(pieces);
        undoButton.setEnabled(!aiThinking && game.canUndo());
    }

    private void showGameOver() {
        if (gameOverShown) {
            return;
        }
        gameOverShown = true;

        String message;
        if (game.getWinner() == 0) {
            message = "Партия окончена: ничья.";
        } else {
            message = "Партия окончена. Победа: " + game.getWinnerName() + ".";
        }
        JOptionPane.showMessageDialog(this, message, "Партия окончена",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
