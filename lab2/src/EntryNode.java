public class EntryNode {

    public int row;

    public int col;

    public int value;

    public EntryNode next;

    public EntryNode(int rowIndex, int colIndex, int elementValue) {
        this.row = rowIndex;
        this.col = colIndex;
        this.value = elementValue;
        this.next = null;
    }
}
