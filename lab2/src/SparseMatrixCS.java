public class SparseMatrixCS {

    private int rows;

    private int cols;

    private EntryNode head;

    private EntryNode tail;

    private int count;

    public SparseMatrixCS(int rowCount, int colCount) {
        this.rows = rowCount;
        this.cols = colCount;
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    public int rowCount() {
        return rows;
    }

    public int colCount() {
        return cols;
    }

    public int nonZeroCount() {
        return count;
    }

    public void set(int rowIndex, int colIndex, int value) {
        EntryNode current = head;
        while (current != null) {
            if (current.row == rowIndex && current.col == colIndex) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        if (value == 0) {
            return;
        }
        EntryNode created = new EntryNode(rowIndex, colIndex, value);
        if (head == null) {
            head = created;
            tail = created;
        } else {
            tail.next = created;
            tail = created;
        }
        count = count + 1;
    }

    // Отсутствующего элемента нет в списке — он нулевой.
    public int get(int rowIndex, int colIndex) {
        EntryNode current = head;
        while (current != null) {
            if (current.row == rowIndex && current.col == colIndex) {
                return current.value;
            }
            current = current.next;
        }
        return 0;
    }

    public EntryNode getHead() {
        return head;
    }
}
