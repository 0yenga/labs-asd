// Узел списка смежности, хранит имя соседней вершины.
public class AdjNode {
    // Имя вершины, в которую ведет дуга.
    public String target;
    // Ссылка на следующий элемент списка смежности.
    public AdjNode next;

    // Создание узла смежности.
    public AdjNode(String targetName) {
        this.target = targetName;
        this.next = null;
    }
}
