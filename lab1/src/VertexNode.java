// Вершина ориентированного графа.
// Вершины связаны в односвязный список через поле next,
// исходящие дуги хранятся в собственном списке смежности.
public class VertexNode {
    // Имя вершины (символ).
    public String name;
    // Голова списка смежности.
    public AdjNode edges;
    // Ссылка на следующую вершину в общем списке вершин.
    public VertexNode next;

    // Создание вершины с заданным именем.
    public VertexNode(String vertexName) {
        this.name = vertexName;
        this.edges = null;
        this.next = null;
    }

    // Добавление дуги к соседней вершине, дубли исключаются.
    public void addEdge(String targetName) {
        AdjNode current = edges;
        while (current != null) {
            if (current.target.equals(targetName)) {
                return;
            }
            current = current.next;
        }
        AdjNode created = new AdjNode(targetName);
        created.next = edges;
        edges = created;
    }
}
