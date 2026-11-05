public class MoveGenerator {
    private static final int[] DR = {-1, -1, 1, 1};
    private static final int[] DC = {-1, 1, -1, 1};

    private static final int MAX_CAPTURES = 16;
    private static final int MAX_PATH = 17;

    public MoveList generate(Board board, int color) {
        MoveList captures = new MoveList();
        boolean[][] visited = new boolean[Board.SIZE][Board.SIZE];
        int[] pathRows = new int[MAX_PATH];
        int[] pathCols = new int[MAX_PATH];
        int[] capturedRows = new int[MAX_CAPTURES];
        int[] capturedCols = new int[MAX_CAPTURES];

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                int piece = board.get(r, c);
                if (Board.colorOf(piece) != color) {
                    continue;
                }

                visited[r][c] = true;
                pathRows[0] = r;
                pathCols[0] = c;
                captureFrom(board, r, c, piece, Board.isMan(piece), visited,
                        pathRows, pathCols, 1, capturedRows, capturedCols, 0,
                        false, captures);
                visited[r][c] = false;
            }
        }

        if (captures.size() > 0) {
            // В русских шашках при выборе из нескольких взятий нужно бить
            // максимальное количество шашек.
            int max = 0;
            for (int i = 0; i < captures.size(); i++) {
                if (captures.get(i).captureCount > max) {
                    max = captures.get(i).captureCount;
                }
            }

            MoveList result = new MoveList();
            for (int i = 0; i < captures.size(); i++) {
                Move move = captures.get(i);
                if (move.captureCount == max) {
                    result.add(move);
                }
            }
            return result;
        }

        MoveList moves = new MoveList();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                int piece = board.get(r, c);
                if (Board.colorOf(piece) != color) {
                    continue;
                }

                if (Board.isMan(piece)) {
                    generateManMoves(board, r, c, piece, color, moves,
                            pathRows, pathCols, capturedRows, capturedCols);
                } else {
                    generateKingMoves(board, r, c, piece, moves,
                            pathRows, pathCols, capturedRows, capturedCols);
                }
            }
        }
        return moves;
    }

    private void generateManMoves(Board board, int r, int c, int piece, int color,
                                  MoveList moves, int[] pathRows, int[] pathCols,
                                  int[] capturedRows, int[] capturedCols) {
        int direction = color == Board.WHITE ? -1 : 1;

        for (int dc = -1; dc <= 1; dc += 2) {
            int nr = r + direction;
            int nc = c + dc;
            if (!Board.inBounds(nr, nc) || board.get(nr, nc) != Board.EMPTY) {
                continue;
            }

            pathRows[0] = r;
            pathCols[0] = c;
            pathRows[1] = nr;
            pathCols[1] = nc;

            boolean promotion = (color == Board.WHITE && nr == 0)
                    || (color == Board.BLACK && nr == Board.SIZE - 1);

            moves.add(new Move(r, c, pathRows, pathCols, 2,
                    capturedRows, capturedCols, 0, promotion, true));
        }
    }

    private void generateKingMoves(Board board, int r, int c, int piece,
                                   MoveList moves, int[] pathRows, int[] pathCols,
                                   int[] capturedRows, int[] capturedCols) {
        for (int d = 0; d < 4; d++) {
            int nr = r + DR[d];
            int nc = c + DC[d];

            while (Board.inBounds(nr, nc) && board.get(nr, nc) == Board.EMPTY) {
                pathRows[0] = r;
                pathCols[0] = c;
                pathRows[1] = nr;
                pathCols[1] = nc;

                moves.add(new Move(r, c, pathRows, pathCols, 2,
                        capturedRows, capturedCols, 0, false, false));

                nr += DR[d];
                nc += DC[d];
            }
        }
    }

    private void captureFrom(Board board, int r, int c, int piece,
                             boolean startedAsMan, boolean[][] visited,
                             int[] pathRows, int[] pathCols, int pathLength,
                             int[] capturedRows, int[] capturedCols, int captureCount,
                             boolean promoted, MoveList out) {
        boolean found = false;
        int color = Board.colorOf(piece);

        if (Board.isMan(piece)) {
            for (int d = 0; d < 4; d++) {
                int enemyRow = r + DR[d];
                int enemyCol = c + DC[d];
                int landingRow = r + 2 * DR[d];
                int landingCol = c + 2 * DC[d];

                if (!Board.inBounds(landingRow, landingCol)) {
                    continue;
                }

                int enemy = board.get(enemyRow, enemyCol);
                if (enemy == Board.EMPTY || Board.colorOf(enemy) == color) {
                    continue;
                }
                if (board.get(landingRow, landingCol) != Board.EMPTY
                        || visited[landingRow][landingCol]) {
                    continue;
                }

                boolean justPromoted = (color == Board.WHITE && landingRow == 0)
                        || (color == Board.BLACK && landingRow == Board.SIZE - 1);
                int nextPiece = justPromoted
                        ? (color == Board.WHITE ? Board.WHITE_KING : Board.BLACK_KING)
                        : piece;

                board.set(enemyRow, enemyCol, Board.EMPTY);
                board.set(r, c, Board.EMPTY);
                board.set(landingRow, landingCol, nextPiece);
                visited[landingRow][landingCol] = true;

                pathRows[pathLength] = landingRow;
                pathCols[pathLength] = landingCol;
                capturedRows[captureCount] = enemyRow;
                capturedCols[captureCount] = enemyCol;

                captureFrom(board, landingRow, landingCol, nextPiece,
                        startedAsMan, visited, pathRows, pathCols, pathLength + 1,
                        capturedRows, capturedCols, captureCount + 1,
                        promoted || justPromoted, out);

                visited[landingRow][landingCol] = false;
                board.set(landingRow, landingCol, Board.EMPTY);
                board.set(r, c, piece);
                board.set(enemyRow, enemyCol, enemy);
                found = true;
            }
        } else {
            for (int d = 0; d < 4; d++) {
                int enemyRow = r + DR[d];
                int enemyCol = c + DC[d];

                while (Board.inBounds(enemyRow, enemyCol)
                        && board.get(enemyRow, enemyCol) == Board.EMPTY) {
                    enemyRow += DR[d];
                    enemyCol += DC[d];
                }

                if (!Board.inBounds(enemyRow, enemyCol)) {
                    continue;
                }

                int enemy = board.get(enemyRow, enemyCol);
                if (Board.colorOf(enemy) == color) {
                    continue;
                }

                int landingRow = enemyRow + DR[d];
                int landingCol = enemyCol + DC[d];

                while (Board.inBounds(landingRow, landingCol)
                        && board.get(landingRow, landingCol) == Board.EMPTY) {
                    if (!visited[landingRow][landingCol]) {
                        board.set(enemyRow, enemyCol, Board.EMPTY);
                        board.set(r, c, Board.EMPTY);
                        board.set(landingRow, landingCol, piece);
                        visited[landingRow][landingCol] = true;

                        pathRows[pathLength] = landingRow;
                        pathCols[pathLength] = landingCol;
                        capturedRows[captureCount] = enemyRow;
                        capturedCols[captureCount] = enemyCol;

                        captureFrom(board, landingRow, landingCol, piece,
                                startedAsMan, visited, pathRows, pathCols, pathLength + 1,
                                capturedRows, capturedCols, captureCount + 1,
                                promoted, out);

                        visited[landingRow][landingCol] = false;
                        board.set(landingRow, landingCol, Board.EMPTY);
                        board.set(r, c, piece);
                        board.set(enemyRow, enemyCol, enemy);
                        found = true;
                    }

                    landingRow += DR[d];
                    landingCol += DC[d];
                }
            }
        }

        if (!found && captureCount > 0) {
            out.add(new Move(pathRows[0], pathCols[0],
                    pathRows, pathCols, pathLength,
                    capturedRows, capturedCols, captureCount,
                    promoted, startedAsMan));
        }
    }
}
