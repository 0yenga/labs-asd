public class VertexList {

    private VertexNode head;

    private VertexNode tail;

    private int count;

    public VertexList() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

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

    public int size() {
        return count;
    }

    public VertexNode getHead() {
        return head;
    }

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
