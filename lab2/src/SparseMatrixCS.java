// Разреженная матрица в координатном формате (CS).
// Хранятся только ненулевые элементы в односвязном списке узлов EntryNode.
// Готовые коллекции JDK здесь не используются.
public class SparseMatrixCS {
    // Количество строк матрицы.
    private int rows;
    // Количество столбцов матрицы.
    private int cols;
    // Голова координатного списка.
    private EntryNode head;
    // Хвост координатного списка.
    private EntryNode tail;
    // Количество ненулевых элементов.
    private int count;

    // Создание пустой матрицы заданного размера.
    public SparseMatrixCS(int rowCount, int colCount) {
        this.rows = rowCount;
        this.cols = colCount;
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    // Количество строк.
    public int rowCount() {
        return rows;
    }

    // Количество столбцов.
    public int colCount() {
        return cols;
    }

    // Количество хранимых ненулевых элементов.
    public int nonZeroCount() {
        return count;
    }

    // Добавление элемента, нулевые значения не хранятся.
    // Если элемент с такими координатами уже есть, значение обновляется.
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

    // Поиск значения по координатам последовательным обходом списка.
    // Возвращает 0, если элемент отсутствует в координатном списке.
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

    // Голова координатного списка для внешнего обхода.
    public EntryNode getHead() {
        return head;
    }
}
