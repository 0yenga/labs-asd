public class MoveList {
    private final Move[] moves;
    private int size;

    public MoveList() {
        this(256);
    }

    public MoveList(int capacity) {
        moves = new Move[capacity];
        size = 0;
    }

    public void add(Move move) {
        if (size < moves.length) {
            moves[size] = move;
            size++;
        }
    }

    public Move get(int index) {
        return moves[index];
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
    }

    public void swap(int i, int j) {
        Move temp = moves[i];
        moves[i] = moves[j];
        moves[j] = temp;
    }
}
