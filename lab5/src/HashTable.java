public class HashTable {

    private static class Node {

        String key;

        String info;

        Node next;

        Node(String key, String info) {
            this.key = key;
            this.info = info;
        }
    }

    private Node[] table;

    private int size;

    public HashTable() {
        this.table = new Node[7];
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return table.length;
    }

    // Функция деления: ключ-СНИЛС переводится в число и берётся остаток.
    private int indexFor(String key) {
        long number = Long.parseLong(key);
        return (int) (number % table.length);
    }

    // Возвращает true, если добавлен новый элемент, и false, если ключ уже был.
    public boolean insert(String key, String info) {
        int index = indexFor(key);
        Node current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.info = info;
                return false;
            }
            current = current.next;
        }

        if (size + 1 > table.length * 3 / 4) {
            rehash();
            index = indexFor(key);
        }

        Node newNode = new Node(key, info);
        newNode.next = table[index];
        table[index] = newNode;
        size = size + 1;
        return true;
    }

    public String find(String key) {
        int index = indexFor(key);
        Node current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.info;
            }
            current = current.next;
        }
        return null;
    }

    public boolean delete(String key) {
        int index = indexFor(key);
        Node previous = null;
        Node current = table[index];
        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size = size - 1;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    public void print() {
        for (int i = 0; i < table.length; i++) {
            System.out.print("[" + i + "]");
            Node current = table[i];
            if (current == null) {
                System.out.println(" нет");
            } else {
                while (current != null) {
                    System.out.print(" " + current.key + " (" + current.info + ")");
                    if (current.next != null) {
                        System.out.print(" ->");
                    }
                    current = current.next;
                }
                System.out.println();
            }
        }
    }

    private void rehash() {
        Node[] oldTable = table;
        int newCapacity = nextPrime(oldTable.length * 2);
        table = new Node[newCapacity];
        size = 0;

        for (int i = 0; i < oldTable.length; i++) {
            Node current = oldTable[i];
            while (current != null) {
                Node next = current.next;
                int index = indexFor(current.key);
                current.next = table[index];
                table[index] = current;
                size = size + 1;
                current = next;
            }
        }

        System.out.println("Рехэширование: " + oldTable.length + " -> " + newCapacity);
    }

    private static int nextPrime(int value) {
        int candidate = value;
        while (!isPrime(candidate)) {
            candidate = candidate + 1;
        }
        return candidate;
    }

    private static boolean isPrime(int value) {
        if (value < 2) {
            return false;
        }
        for (int i = 2; i * i <= value; i = i + 1) {
            if (value % i == 0) {
                return false;
            }
        }
        return true;
    }
}
