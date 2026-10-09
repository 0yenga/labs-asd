// Односвязный список строк для хранения результата обхода.
// Реализован вручную на узлах ListNode.
public class StringList {
    // Голова списка.
    private ListNode head;
    // Хвост списка для быстрой вставки в конец.
    private ListNode tail;
    // Количество элементов списка.
    private int count;

    // Создание пустого списка.
    public StringList() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

    // Добавление значения в конец списка.
    public void append(String value) {
        ListNode created = new ListNode(value);
        if (head == null) {
            head = created;
            tail = created;
        } else {
            tail.next = created;
            tail = created;
        }
        count = count + 1;
    }

    // Количество элементов списка.
    public int size() {
        return count;
    }

    // Признак пустого списка.
    public boolean isEmpty() {
        return head == null;
    }

    // Получение элемента по порядковому номеру (обход от головы).
    public String getAt(int index) {
        ListNode current = head;
        int position = 0;
        while (current != null) {
            if (position == index) {
                return current.data;
            }
            current = current.next;
            position = position + 1;
        }
        return null;
    }

    // Голова списка для внешнего обхода.
    public ListNode getHead() {
        return head;
    }
}
