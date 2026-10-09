public class LinkedStack {

    private static class StackNode {
        String data;
        StackNode next;

        StackNode(String value) {
            this.data = value;
            this.next = null;
        }
    }

    private StackNode top;

    private int count;

    public LinkedStack() {
        this.top = null;
        this.count = 0;
    }

    public void push(String value) {
        StackNode created = new StackNode(value);
        created.next = top;
        top = created;
        count = count + 1;
    }

    // Извлечение из пустого стека возвращает null.
    public String pop() {
        if (top == null) {
            return null;
        }
        String value = top.data;
        top = top.next;
        count = count - 1;
        return value;
    }

    public boolean isEmpty() {
        return top == null;
    }
}
