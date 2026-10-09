public class StringList {

    private ListNode head;

    private ListNode tail;

    private int count;

    public StringList() {
        this.head = null;
        this.tail = null;
        this.count = 0;
    }

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

    public boolean contains(String value) {
        ListNode current = head;
        while (current != null) {
            if (current.data.equals(value)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return head == null;
    }

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

    public ListNode getHead() {
        return head;
    }
}
