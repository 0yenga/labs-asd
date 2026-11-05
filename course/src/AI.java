public class AI {
    private static final int INF = 1_000_000;
    private static final int MATE = 100_000;

    private final MoveGenerator generator = new MoveGenerator();
    private final int maxDepth;
    private int rootDepth;

    public AI(int depth) {
        this.maxDepth = depth;
    }

    public Move chooseMove(Board board, int color) {
        MoveList moves = generator.generate(board, color);
        if (moves.size() == 0) {
            return null;
        }

        orderMoves(moves);
        rootDepth = maxDepth;
        Move bestMove = moves.get(0);

        if (color == Board.WHITE) {
            int bestScore = -INF;
            int alpha = -INF;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Board next = board.copy();
                next.apply(move);
                int score = minimax(next, maxDepth - 1, alpha, INF, Board.BLACK);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                if (bestScore > alpha) {
                    alpha = bestScore;
                }
            }
        } else {
            int bestScore = INF;
            int beta = INF;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Board next = board.copy();
                next.apply(move);
                int score = minimax(next, maxDepth - 1, -INF, beta, Board.WHITE);
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                if (bestScore < beta) {
                    beta = bestScore;
                }
            }
        }

        return bestMove;
    }

    private int minimax(Board board, int remainingDepth, int alpha, int beta, int turn) {
        MoveList moves = generator.generate(board, turn);

        if (moves.size() == 0) {
            int ply = rootDepth - remainingDepth;
            if (turn == Board.WHITE) {
                return -MATE + ply;
            }
            return MATE - ply;
        }

        if (remainingDepth == 0) {
            return evaluate(board);
        }

        orderMoves(moves);

        if (turn == Board.WHITE) {
            int best = -INF;
            for (int i = 0; i < moves.size(); i++) {
                Board next = board.copy();
                next.apply(moves.get(i));
                int score = minimax(next, remainingDepth - 1, alpha, beta, Board.BLACK);
                if (score > best) {
                    best = score;
                }
                if (best > alpha) {
                    alpha = best;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return best;
        }

        int best = INF;
        for (int i = 0; i < moves.size(); i++) {
            Board next = board.copy();
            next.apply(moves.get(i));
            int score = minimax(next, remainingDepth - 1, alpha, beta, Board.WHITE);
            if (score < best) {
                best = score;
            }
            if (best < beta) {
                beta = best;
            }
            if (alpha >= beta) {
                break;
            }
        }
        return best;
    }

    private void orderMoves(MoveList moves) {
        // Сортировка вставками: сначала взятия, затем превращения в дамку.
        for (int i = 1; i < moves.size(); i++) {
            Move current = moves.get(i);
            int j = i - 1;
            while (j >= 0 && moveScore(moves.get(j)) < moveScore(current)) {
                moves.swap(j, j + 1);
                j--;
            }
        }
    }

    private int moveScore(Move move) {
        int score = move.captureCount * 1000;
        if (move.promotion) {
            score += 200;
        }
        if (move.captureCount == 0) {
            score += 10;
        }
        return score;
    }

    private int evaluate(Board board) {
        int score = 0;

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                int piece = board.get(r, c);
                if (piece == Board.EMPTY) {
                    continue;
                }

                int sign = piece > 0 ? 1 : -1;
                int type = Math.abs(piece);
                int value = type == 2 ? 320 : 100;

                if (type == 1) {
                    int advance = piece > 0 ? (Board.SIZE - 1 - r) : r;
                    value += advance * 4;
                }

                double centerDistance = Math.abs(r - 3.5) + Math.abs(c - 3.5);
                value += (int) ((7.0 - centerDistance) * 2);

                score += sign * value;
            }
        }

        return score;
    }
}
