import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class BoardPanel extends JPanel {
    private static final int MARGIN_X = 28;
    private static final int MARGIN_TOP = 20;
    private static final int MARGIN_BOTTOM = 56;
    private static final Color LIGHT_SQUARE = new Color(238, 216, 184);
    private static final Color DARK_SQUARE = new Color(116, 78, 50);
    private static final Color WHITE_PIECE = new Color(250, 250, 245);
    private static final Color BLACK_PIECE = new Color(35, 35, 35);
    private static final Color HIGHLIGHT = new Color(255, 220, 80, 150);
    private static final Color NEXT_STEP = new Color(70, 190, 90, 160);
    private static final Color CAPTURED = new Color(220, 60, 60, 150);
    private static final Color LAST_MOVE = new Color(70, 130, 200, 70);
    private static final Color PATH = new Color(190, 40, 40, 180);

    private Game game;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private MoveList candidates;
    private int[] pathRows = new int[17];
    private int[] pathCols = new int[17];
    private int pathLength;

    private int cursorRow = 5;
    private int cursorCol = 0;

    private boolean inputEnabled = true;
    private Runnable onMove;

    public BoardPanel() {
        setBackground(new Color(245, 240, 230));
        setFocusable(true);
        setPreferredSize(new Dimension(640, 640));
        installMouse();
        installKeys();
    }

    public void setGame(Game game) {
        this.game = game;
        clearSelection();
        cursorRow = 5;
        cursorCol = 0;
        repaint();
    }

    public void setOnMove(Runnable onMove) {
        this.onMove = onMove;
    }

    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
        if (!enabled) {
            clearSelection();
        }
        repaint();
    }

    public void clearSelection() {
        selectedRow = -1;
        selectedCol = -1;
        candidates = null;
        pathLength = 0;
        repaint();
    }

    private void installMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                int[] cell = toCell(e.getX(), e.getY());
                if (cell != null) {
                    cursorRow = cell[0];
                    cursorCol = cell[1];
                    handleSquare(cell[0], cell[1]);
                }
            }
        });
    }

    private void installKeys() {
        bind("LEFT", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(0, -1);
            }
        });
        bind("RIGHT", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(0, 1);
            }
        });
        bind("UP", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(-1, 0);
            }
        });
        bind("DOWN", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                moveCursor(1, 0);
            }
        });
        bind("ENTER", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleSquare(cursorRow, cursorCol);
            }
        });
        bind("SPACE", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                handleSquare(cursorRow, cursorCol);
            }
        });
        bind("ESCAPE", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                clearSelection();
            }
        });
    }

    private void bind(String key, AbstractAction action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        getActionMap().put(key, action);
    }

    private void moveCursor(int dr, int dc) {
        cursorRow += dr;
        cursorCol += dc;
        if (cursorRow < 0) {
            cursorRow = 0;
        }
        if (cursorRow > 7) {
            cursorRow = 7;
        }
        if (cursorCol < 0) {
            cursorCol = 0;
        }
        if (cursorCol > 7) {
            cursorCol = 7;
        }
        repaint();
    }

    private void handleSquare(int r, int c) {
        if (!inputEnabled || game == null || game.isGameOver()) {
            return;
        }

        if (selectedRow < 0) {
            selectPiece(r, c);
            return;
        }

        if (r == selectedRow && c == selectedCol && pathLength == 1) {
            clearSelection();
            return;
        }

        MoveList next = new MoveList();
        for (int i = 0; i < candidates.size(); i++) {
            Move move = candidates.get(i);
            if (pathLength < move.pathLength
                    && move.pathRows[pathLength] == r
                    && move.pathCols[pathLength] == c) {
                next.add(move);
            }
        }

        if (next.size() == 0) {
            if (pathLength == 1) {
                clearSelection();
                selectPiece(r, c);
            }
            return;
        }

        pathRows[pathLength] = r;
        pathCols[pathLength] = c;
        pathLength++;
        candidates = next;

        boolean allComplete = true;
        for (int i = 0; i < candidates.size(); i++) {
            if (candidates.get(i).pathLength != pathLength) {
                allComplete = false;
            }
        }

        if (allComplete) {
            Move move = candidates.get(0);
            clearSelection();
            if (game.makeMove(move)) {
                repaint();
                if (onMove != null) {
                    onMove.run();
                }
            }
        } else {
            repaint();
        }
    }

    private void selectPiece(int r, int c) {
        int piece = game.getBoard().get(r, c);
        if (piece == Board.EMPTY || Board.colorOf(piece) != game.getTurn()) {
            return;
        }

        MoveList from = new MoveList();
        MoveList legal = game.getLegalMoves();
        for (int i = 0; i < legal.size(); i++) {
            Move move = legal.get(i);
            if (move.fromRow == r && move.fromCol == c) {
                from.add(move);
            }
        }

        if (from.size() == 0) {
            return;
        }

        selectedRow = r;
        selectedCol = c;
        candidates = from;
        pathRows[0] = r;
        pathCols[0] = c;
        pathLength = 1;
        cursorRow = r;
        cursorCol = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (game == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cell = cellSize();
        int ox = offsetX(cell);
        int oy = offsetY(cell);

        drawBoard(g2, ox, oy, cell);
        drawLastMove(g2, ox, oy, cell);
        drawSelection(g2, ox, oy, cell);
        drawPieces(g2, ox, oy, cell);
        drawPath(g2, ox, oy, cell);
        drawCoordinates(g2, ox, oy, cell);

        if (isFocusOwner()) {
            g2.setColor(new Color(240, 140, 20));
            g2.setStroke(new BasicStroke(3f));
            g2.drawRect(ox + cursorCol * cell + 2, oy + cursorRow * cell + 2,
                    cell - 4, cell - 4);
        }

        g2.setColor(new Color(80, 55, 35));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(ox, oy, cell * 8, cell * 8);
    }

    private void drawBoard(Graphics2D g2, int ox, int oy, int cell) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                g2.setColor(Board.isDark(r, c) ? DARK_SQUARE : LIGHT_SQUARE);
                g2.fillRect(ox + c * cell, oy + r * cell, cell, cell);
            }
        }
    }

    private void drawLastMove(Graphics2D g2, int ox, int oy, int cell) {
        Move last = game.getLastMove();
        if (last == null) {
            return;
        }
        g2.setColor(LAST_MOVE);
        for (int i = 0; i < last.pathLength; i++) {
            int r = last.pathRows[i];
            int c = last.pathCols[i];
            g2.fillRect(ox + c * cell, oy + r * cell, cell, cell);
        }
    }

    private void drawSelection(Graphics2D g2, int ox, int oy, int cell) {
        if (selectedRow < 0 || candidates == null) {
            return;
        }

        g2.setColor(HIGHLIGHT);
        g2.fillRect(ox + selectedCol * cell, oy + selectedRow * cell, cell, cell);

        boolean[][] next = new boolean[8][8];
        boolean[][] captured = new boolean[8][8];

        for (int i = 0; i < candidates.size(); i++) {
            Move move = candidates.get(i);
            if (pathLength < move.pathLength) {
                next[move.pathRows[pathLength]][move.pathCols[pathLength]] = true;
            }
            for (int j = 0; j < pathLength - 1 && j < move.captureCount; j++) {
                captured[move.capturedRows[j]][move.capturedCols[j]] = true;
            }
        }

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (next[r][c]) {
                    g2.setColor(NEXT_STEP);
                    g2.fillRect(ox + c * cell, oy + r * cell, cell, cell);
                }
                if (captured[r][c]) {
                    g2.setColor(CAPTURED);
                    g2.fillRect(ox + c * cell, oy + r * cell, cell, cell);
                }
            }
        }
    }

    private void drawPieces(Graphics2D g2, int ox, int oy, int cell) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int piece = game.getBoard().get(r, c);
                if (piece != Board.EMPTY) {
                    drawPiece(g2, piece, ox + c * cell + cell / 2,
                            oy + r * cell + cell / 2, cell);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2, int piece, int cx, int cy, int cell) {
        int diameter = (int) (cell * 0.72);
        int x = cx - diameter / 2;
        int y = cy - diameter / 2;

        if (piece > 0) {
            g2.setColor(WHITE_PIECE);
            g2.fill(new Ellipse2D.Double(x, y, diameter, diameter));
            g2.setColor(new Color(60, 60, 60));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new Ellipse2D.Double(x, y, diameter, diameter));
        } else {
            g2.setColor(BLACK_PIECE);
            g2.fill(new Ellipse2D.Double(x, y, diameter, diameter));
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new Ellipse2D.Double(x, y, diameter, diameter));
        }

        if (Board.isKing(piece)) {
            drawCrown(g2, cx, cy, diameter, piece > 0);
        }
    }

    private void drawCrown(Graphics2D g2, int cx, int cy, int diameter, boolean white) {
        int r = diameter / 2;
        int[] xs = {
                cx - r * 55 / 100, cx - r * 40 / 100, cx - r * 15 / 100,
                cx, cx + r * 15 / 100, cx + r * 40 / 100, cx + r * 55 / 100
        };
        int[] ys = {
                cy + r * 35 / 100, cy - r * 30 / 100, cy + r * 5 / 100,
                cy - r * 45 / 100, cy + r * 5 / 100, cy - r * 30 / 100,
                cy + r * 35 / 100
        };
        g2.setColor(white ? new Color(170, 120, 20) : new Color(235, 200, 70));
        g2.fill(new Polygon(xs, ys, xs.length));
    }

    private void drawPath(Graphics2D g2, int ox, int oy, int cell) {
        if (pathLength < 2) {
            return;
        }

        g2.setColor(PATH);
        g2.setStroke(new BasicStroke(3f));
        for (int i = 1; i < pathLength; i++) {
            double x1 = ox + pathCols[i - 1] * cell + cell / 2.0;
            double y1 = oy + pathRows[i - 1] * cell + cell / 2.0;
            double x2 = ox + pathCols[i] * cell + cell / 2.0;
            double y2 = oy + pathRows[i] * cell + cell / 2.0;
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        int lastRow = pathRows[pathLength - 1];
        int lastCol = pathCols[pathLength - 1];
        g2.setColor(new Color(255, 255, 255, 180));
        int d = (int) (cell * 0.35);
        g2.fill(new Ellipse2D.Double(ox + lastCol * cell + cell / 2.0 - d / 2.0,
                oy + lastRow * cell + cell / 2.0 - d / 2.0, d, d));
    }

    private void drawCoordinates(Graphics2D g2, int ox, int oy, int cell) {
        g2.setColor(new Color(80, 55, 35));
        g2.setFont(new Font("SansSerif", Font.PLAIN, Math.max(10, cell / 4)));

        for (int c = 0; c < 8; c++) {
            String text = String.valueOf((char) ('a' + c));
            int width = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, ox + c * cell + (cell - width) / 2, oy + cell * 8 + cell / 2 + 4);
        }
        for (int r = 0; r < 8; r++) {
            String text = String.valueOf(8 - r);
            int width = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, ox - width - 8, oy + r * cell + cell / 2 + 5);
        }
    }

    private int cellSize() {
        int width = getWidth() - 2 * MARGIN_X;
        int height = getHeight() - MARGIN_TOP - MARGIN_BOTTOM;
        int size = Math.min(width, height);
        if (size < 80) {
            size = Math.min(getWidth(), getHeight());
        }
        return Math.max(1, size / 8);
    }

    private int offsetX(int cell) {
        return (getWidth() - cell * 8) / 2;
    }

    private int offsetY(int cell) {
        int available = getHeight() - MARGIN_TOP - MARGIN_BOTTOM;
        return MARGIN_TOP + (available - cell * 8) / 2;
    }

    private int[] toCell(int x, int y) {
        int cell = cellSize();
        int ox = offsetX(cell);
        int oy = offsetY(cell);
        if (x < ox || y < oy || x >= ox + cell * 8 || y >= oy + cell * 8) {
            return null;
        }
        return new int[]{(y - oy) / cell, (x - ox) / cell};
    }
}
