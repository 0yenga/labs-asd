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
        EntryNode previous = null;
        EntryNode current = head;
        while (current != null) {
            if (current.row == rowIndex && current.col == colIndex) {
                if (value != 0) {
                    current.value = value;
                    return;
                }
                // Запись нуля удаляет узел, иначе count и обход будут неверными.
                if (previous == null) {
                    head = current.next;
                } else {
                    previous.next = current.next;
                }
                if (current == tail) {
                    tail = previous;
                }
                count = count - 1;
                return;
            }
            previous = current;
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
