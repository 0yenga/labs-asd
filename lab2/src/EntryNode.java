// Узел координатного списка разреженной матрицы.
// Хранит координаты и значение одного ненулевого элемента.
public class EntryNode {
    // Номер строки элемента.
    public int row;
    // Номер столбца элемента.
    public int col;
    // Значение элемента.
    public int value;
    // Ссылка на следующий узел списка.
    public EntryNode next;

    // Создание узла с заданными координатами и значением.
    public EntryNode(int rowIndex, int colIndex, int elementValue) {
        this.row = rowIndex;
        this.col = colIndex;
        this.value = elementValue;
        this.next = null;
    }
}
