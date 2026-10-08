// Стек строк на односвязном списке, используется для поиска в глубину.
// Готовый класс Stack из JDK здесь специально не применяется.
public class LinkedStack {
    // Внутренний узел стека.
    private static class StackNode {
        String data;
        StackNode next;

        StackNode(String value) {
            this.data = value;
            this.next = null;
        }
    }

    // Вершина стека.
    private StackNode top;
    // Количество элементов.
    private int count;

    // Создание пустого стека.
    public LinkedStack() {
        this.top = null;
        this.count = 0;
    }

    // Добавление элемента на вершину стека.
    public void push(String value) {
        StackNode created = new StackNode(value);
        created.next = top;
        top = created;
        count = count + 1;
    }

    // Извлечение элемента с вершины стека.
    public String pop() {
        if (top == null) {
            return null;
        }
        String value = top.data;
        top = top.next;
        count = count - 1;
        return value;
    }

    // Признак пустого стека.
    public boolean isEmpty() {
        return top == null;
    }
}
