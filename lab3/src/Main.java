import java.util.Scanner;

public class Main {

    // Высота поддерева: количество вершин на самом длинном пути от корня до листа.
    // Лист имеет высоту 1.
    static int height(Node node) {
        if (node == null) {
            return 0;
        }
        if (node.left == null && node.right == null) {
            return 1;
        }
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);
        if (leftHeight > rightHeight) {
            return 1 + leftHeight;
        }
        return 1 + rightHeight;
    }

    // Количество листьев в поддереве.
    static int leaves(Node node) {
        if (node == null) {
            return 0;
        }
        if (node.left == null && node.right == null) {
            return 1;
        }
        return leaves(node.left) + leaves(node.right);
    }

    static double ratio(Node node) {
        return (double) height(node) / leaves(node);
    }

    static double maxRatio(Node node) {
        if (node == null) {
            return -1;
        }
        double best = ratio(node);
        double leftBest = maxRatio(node.left);
        double rightBest = maxRatio(node.right);
        if (leftBest > best) {
            best = leftBest;
        }
        if (rightBest > best) {
            best = rightBest;
        }
        return best;
    }

    static double minRatio(Node node) {
        if (node == null) {
            return Double.MAX_VALUE;
        }
        double best = ratio(node);
        double leftBest = minRatio(node.left);
        double rightBest = minRatio(node.right);
        if (leftBest < best) {
            best = leftBest;
        }
        if (rightBest < best) {
            best = rightBest;
        }
        return best;
    }

    static void printTree(Node node, int level) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println(node.value);
        printTree(node.left, level + 1);
        printTree(node.right, level + 1);
    }

    static void printByRatio(Node node, double target) {
        if (node == null) {
            return;
        }
        double current = ratio(node);
        if (Math.abs(current - target) < 1e-9) {
            System.out.println("  вершина " + node.value
                    + ": height=" + height(node)
                    + ", leaves=" + leaves(node)
                    + ", ratio=" + current);
        }
        printByRatio(node.left, target);
        printByRatio(node.right, target);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите N, затем N строк: значение left right (0 = нет вершины).");
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
        int[] leftIndex = new int[n + 1];
        int[] rightIndex = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            int value = scanner.nextInt();
            leftIndex[i] = scanner.nextInt();
            rightIndex[i] = scanner.nextInt();
            nodes[i] = new Node(value);
        }

        for (int i = 1; i <= n; i++) {
            if (leftIndex[i] < 0 || leftIndex[i] > n || rightIndex[i] < 0 || rightIndex[i] > n) {
                System.out.println("Ошибка: номер вершины вне диапазона 0.." + n + ".");
                return;
            }
            if (leftIndex[i] != 0) {
                nodes[i].left = nodes[leftIndex[i]];
            }
            if (rightIndex[i] != 0) {
                nodes[i].right = nodes[rightIndex[i]];
            }
        }

        Node root = nodes[1];
        System.out.println("Дерево:");
        printTree(root, 0);

        double max = maxRatio(root);
        double min = minRatio(root);

        System.out.println("Максимальное отношение height/leaves = " + max);
        printByRatio(root, max);

        System.out.println("Минимальное отношение height/leaves = " + min);
        printByRatio(root, min);
    }
}
