public class Node {

    int value;

    Node firstChild;

    Node nextSibling;

    Node(int value) {
        this.value = value;
    }

    // Добавляет ребёнка в конец списка детей.
    void addChild(Node child) {
        if (firstChild == null) {
            firstChild = child;
            return;
        }
        Node current = firstChild;
        while (current.nextSibling != null) {
            current = current.nextSibling;
        }
        current.nextSibling = child;
    }
}
