public class Move {
    public final int fromRow;
    public final int fromCol;
    public final int toRow;
    public final int toCol;

    // Полный путь: откуда, затем каждая клетка приземления после прыжка.
    public final int[] pathRows;
    public final int[] pathCols;
    public final int pathLength;

    // Клетки с побитыми шашками.
    public final int[] capturedRows;
    public final int[] capturedCols;
    public final int captureCount;

    public final boolean promotion;
    public final boolean manMoved;

    public Move(int fromRow, int fromCol,
                int[] pathRows, int[] pathCols, int pathLength,
                int[] capturedRows, int[] capturedCols, int captureCount,
                boolean promotion, boolean manMoved) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.pathLength = pathLength;
        this.captureCount = captureCount;
        this.promotion = promotion;
        this.manMoved = manMoved;

        this.pathRows = new int[pathLength];
        this.pathCols = new int[pathLength];
        for (int i = 0; i < pathLength; i++) {
            this.pathRows[i] = pathRows[i];
            this.pathCols[i] = pathCols[i];
        }

        this.capturedRows = new int[captureCount];
        this.capturedCols = new int[captureCount];
        for (int i = 0; i < captureCount; i++) {
            this.capturedRows[i] = capturedRows[i];
            this.capturedCols[i] = capturedCols[i];
        }

        this.toRow = pathRows[pathLength - 1];
        this.toCol = pathCols[pathLength - 1];
    }

    public int nextRow(int step) {
        return pathRows[step];
    }

    public int nextCol(int step) {
        return pathCols[step];
    }

    public String squareName(int r, int c) {
        char column = (char) ('a' + c);
        int row = Board.SIZE - r;
        return "" + column + row;
    }

    @Override
    public String toString() {
        String text = squareName(fromRow, fromCol) + "-" + squareName(toRow, toCol);
        if (captureCount > 0) {
            text += " x" + captureCount;
        }
        if (promotion) {
            text += " (дамка)";
        }
        return text;
    }
}
