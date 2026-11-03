import java.util.Scanner;

public class Main {

    // Высота N-дерева в вершинах: лист имеет высоту 1.
    static int height(Node node) {
        if (node == null) {
            return 0;
        }
        int best = 0;
        Node child = node.firstChild;
        while (child != null) {
            int childHeight = height(child);
            if (childHeight > best) {
                best = childHeight;
            }
            child = child.nextSibling;
        }
        return 1 + best;
    }

    static void printTree(Node node, int level) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println(node.value);
        Node child = node.firstChild;
        while (child != null) {
            printTree(child, level + 1);
            child = child.nextSibling;
        }
    }

    // depth — глубина текущей вершины, корень имеет глубину 1.
    // Удаляются только листья, находящиеся на максимальной глубине.
    static void removeDeepestLeaves(Node node, int depth, int maxDepth) {
        Node previous = null;
        Node current = node.firstChild;
        while (current != null) {
            Node next = current.nextSibling;
            boolean isLeaf = current.firstChild == null;
            if (isLeaf && depth + 1 == maxDepth) {
                if (previous == null) {
                    node.firstChild = next;
                } else {
                    previous.nextSibling = next;
                }
                System.out.println("Удалена вершина " + current.value
                        + " (глубина " + (depth + 1) + ")");
            } else {
                removeDeepestLeaves(current, depth + 1, maxDepth);
                previous = current;
            }
            current = next;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите N, затем N строк: значение parent (0 = корень).");
        if (!scanner.hasNextInt()) {
            System.out.println("Ошибка: не найдено количество вершин N.");
            return;
        }
        int n = scanner.nextInt();
        if (n <= 0) {
            System.out.println("Дерево пусто.");
            return;
        }

        Node[] nodes = new Node[n + 1];
        int[] parent = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            int value = scanner.nextInt();
            parent[i] = scanner.nextInt();
            nodes[i] = new Node(value);
        }

        Node root = null;
        for (int i = 1; i <= n; i++) {
            if (parent[i] == 0) {
                if (root != null) {
                    System.out.println("Ошибка: найдено несколько корней.");
                    return;
                }
                root = nodes[i];
            } else {
                if (parent[i] < 1 || parent[i] > n || parent[i] == i) {
                    System.out.println("Ошибка: некорректный parent у вершины " + i + ".");
                    return;
                }
                nodes[parent[i]].addChild(nodes[i]);
            }
        }

        if (root == null) {
            System.out.println("Ошибка: корень не найден.");
            return;
        }

        System.out.println("Исходное дерево:");
        printTree(root, 0);

        int maxDepth = height(root);
        System.out.println("Максимальная глубина: " + maxDepth);

        if (maxDepth == 1) {
            System.out.println("Корень является единственным листом, дерево становится пустым.");
            root = null;
        } else {
            removeDeepestLeaves(root, 1, maxDepth);
        }

        System.out.println("Дерево после удаления:");
        if (root == null) {
            System.out.println("(пусто)");
        } else {
            printTree(root, 0);
        }
    }
}
