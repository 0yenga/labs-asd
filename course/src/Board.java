public class Board {
    public static final int EMPTY = 0;
    public static final int WHITE_MAN = 1;
    public static final int WHITE_KING = 2;
    public static final int BLACK_MAN = -1;
    public static final int BLACK_KING = -2;

    public static final int WHITE = 1;
    public static final int BLACK = -1;

    public static final int SIZE = 8;

    private final int[][] cells;

    public Board() {
        cells = new int[SIZE][SIZE];
        setup();
    }

    private Board(int[][] other) {
        cells = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = other[r][c];
            }
        }
    }

    public void setup() {
        clear();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (isDark(r, c)) {
                    cells[r][c] = BLACK_MAN;
                }
            }
        }
        for (int r = 5; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (isDark(r, c)) {
                    cells[r][c] = WHITE_MAN;
                }
            }
        }
    }

    public void clear() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = EMPTY;
            }
        }
    }

    public Board copy() {
        return new Board(cells);
    }

    public int get(int r, int c) {
        return cells[r][c];
    }

    public void set(int r, int c, int value) {
        cells[r][c] = value;
    }

    public static boolean inBounds(int r, int c) {
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    public static boolean isDark(int r, int c) {
        return (r + c) % 2 == 1;
    }

    public static int colorOf(int piece) {
        if (piece > 0) {
            return WHITE;
        }
        if (piece < 0) {
            return BLACK;
        }
        return 0;
    }

    public static boolean isKing(int piece) {
        return piece == WHITE_KING || piece == BLACK_KING;
    }

    public static boolean isMan(int piece) {
        return piece == WHITE_MAN || piece == BLACK_MAN;
    }

    public int count(int color) {
        int result = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (colorOf(cells[r][c]) == color) {
                    result++;
                }
            }
        }
        return result;
    }

    public int countKings(int color) {
        int result = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int piece = cells[r][c];
                if (colorOf(piece) == color && isKing(piece)) {
                    result++;
                }
            }
        }
        return result;
    }

    // Применяет уже проверенный ход к позиции.
    public void apply(Move move) {
        int piece = get(move.fromRow, move.fromCol);
        set(move.fromRow, move.fromCol, EMPTY);

        for (int i = 0; i < move.captureCount; i++) {
            set(move.capturedRows[i], move.capturedCols[i], EMPTY);
        }

        int placed = piece;
        if (move.promotion) {
            placed = piece > 0 ? WHITE_KING : BLACK_KING;
        }
        set(move.toRow, move.toCol, placed);
    }

    public String squareName(int r, int c) {
        char column = (char) ('a' + c);
        int row = SIZE - r;
        return "" + column + row;
    }
}
