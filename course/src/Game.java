public class Game {
    private static final int MAX_HISTORY = 512;
    private static final int DRAW_PLIES = 60; // 30 ходов без взятия и без хода простой шашкой

    private final MoveGenerator generator = new MoveGenerator();

    private Board board;
    private int turn;
    private Move lastMove;
    private MoveList legalMoves;
    private boolean gameOver;
    private int winner; // 0 — ничья, иначе цвет победителя
    private int plyWithoutProgress;

    private final Board[] historyBoards = new Board[MAX_HISTORY];
    private final int[] historyTurns = new int[MAX_HISTORY];
    private final Move[] historyLastMoves = new Move[MAX_HISTORY];
    private final int[] historyProgress = new int[MAX_HISTORY];
    private int historySize;

    public Game() {
        newGame();
    }

    public void newGame() {
        board = new Board();
        turn = Board.WHITE;
        lastMove = null;
        gameOver = false;
        winner = 0;
        plyWithoutProgress = 0;
        historySize = 0;
        legalMoves = generator.generate(board, turn);
        checkGameOver();
    }

    public Board getBoard() {
        return board;
    }

    public int getTurn() {
        return turn;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public MoveList getLegalMoves() {
        return legalMoves;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public boolean canUndo() {
        return historySize > 0;
    }

    public boolean makeMove(Move move) {
        if (gameOver || move == null) {
            return false;
        }

        if (historySize < MAX_HISTORY) {
            historyBoards[historySize] = board.copy();
            historyTurns[historySize] = turn;
            historyLastMoves[historySize] = lastMove;
            historyProgress[historySize] = plyWithoutProgress;
            historySize++;
        }

        board.apply(move);
        if (move.captureCount > 0 || move.manMoved) {
            plyWithoutProgress = 0;
        } else {
            plyWithoutProgress++;
        }

        lastMove = move;
        turn = -turn;
        legalMoves = generator.generate(board, turn);
        checkGameOver();
        return true;
    }

    public boolean undo() {
        if (historySize == 0) {
            return false;
        }

        historySize--;
        board = historyBoards[historySize];
        turn = historyTurns[historySize];
        lastMove = historyLastMoves[historySize];
        plyWithoutProgress = historyProgress[historySize];
        gameOver = false;
        winner = 0;
        legalMoves = generator.generate(board, turn);
        checkGameOver();
        return true;
    }

    private void checkGameOver() {
        if (legalMoves.size() == 0) {
            gameOver = true;
            winner = -turn;
            return;
        }
        if (plyWithoutProgress >= DRAW_PLIES) {
            gameOver = true;
            winner = 0;
        }
    }

    public String getTurnName() {
        return turn == Board.WHITE ? "белые" : "чёрные";
    }

    public String getWinnerName() {
        if (winner == Board.WHITE) {
            return "белые";
        }
        if (winner == Board.BLACK) {
            return "чёрные";
        }
        return "ничья";
    }
}
