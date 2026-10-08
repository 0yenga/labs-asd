// Список вершин графа на односвязном списке.
// Порядок вершин соответствует порядку первого появления во входных данных.
public class VertexList {
    // Голова списка вершин.
    private VertexNode head;
    // Хвост списка вершин.
    private VertexNode tail;
    // Количество вершин.
    private int count;

    // Создание пустого списка вершин.
    public VertexList() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    // Поиск вершины по имени последовательным обходом.
    public VertexNode find(String name) {
        VertexNode current = head;
        while (current != null) {
            if (current.name.equals(name)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    // Получение вершины по имени, при отсутствии vertex создается.
    public VertexNode getOrCreate(String name) {
        VertexNode found = find(name);
        if (found != null) {
            return found;
        }
        VertexNode created = new VertexNode(name);
        if (head == null) {
            head = created;
            tail = created;
        } else {
            tail.next = created;
            tail = created;
        }
        count = count + 1;
        return created;
    }

    // Количество вершин.
    public int size() {
        return count;
    }

    // Голова списка для обхода.
    public VertexNode getHead() {
        return head;
    }

    // Вершина по порядковому номеру.
    public VertexNode getAt(int index) {
        VertexNode current = head;
        int position = 0;
        while (current != null) {
            if (position == index) {
                return current;
            }
            current = current.next;
            position = position + 1;
        }
        return null;
    }
}
