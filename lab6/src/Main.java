import java.util.Scanner;

public class Main {

    // Сортировка выбором по убыванию: она делает порядок камней
    // определённым и удобным для вывода.
    static void sortDescending(int[] values) {
        for (int i = 0; i < values.length - 1; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < values.length; j++) {
                if (values[j] > values[maxIndex]) {
                    maxIndex = j;
                }
            }
            int temp = values[i];
            values[i] = values[maxIndex];
            values[maxIndex] = temp;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите N, затем N весов камней.");
        if (!scanner.hasNextInt()) {
            System.out.println("Ошибка: не найдено количество камней N.");
            return;
        }
        int n = scanner.nextInt();
        if (n <= 0) {
            System.out.println("Нет камней.");
            return;
        }

        int[] weights = new int[n];
        long total = 0;
        for (int i = 0; i < n; i++) {
            if (!scanner.hasNextInt()) {
                System.out.println("Ошибка: весов меньше, чем N.");
                return;
            }
            weights[i] = scanner.nextInt();
            if (weights[i] <= 0) {
                System.out.println("Ошибка: веса должны быть положительными.");
                return;
            }
            total = total + weights[i];
        }

        sortDescending(weights);

        // Нужна сумма s одной кучи, для которой:
        // total / 3 <= s <= total / 2.
        // Тогда вторая куча равна total - s, и отношение не больше 2.
        long lowLong = (total + 2) / 3;
        long highLong = total / 2;
        if (lowLong > highLong) {
            System.out.println("Разбить нельзя.");
            return;
        }

        // Точная задача о подмножествах: reachable[s] == true,
        // если из камней можно набрать сумму s.
        if (total > 5000000) {
            System.out.println("Суммарный вес слишком велик для точного решения этим способом.");
            return;
        }

        int totalInt = (int) total;
        int low = (int) lowLong;
        int high = (int) highLong;
        boolean[] reachable = new boolean[totalInt + 1];
        int[] lastIndex = new int[totalInt + 1];
        reachable[0] = true;

        for (int i = 0; i < n; i++) {
            int weight = weights[i];
            for (int sum = totalInt; sum >= weight; sum = sum - 1) {
                if (!reachable[sum] && reachable[sum - weight]) {
                    reachable[sum] = true;
                    lastIndex[sum] = i;
                }
            }
        }

        int best = -1;
        for (int sum = high; sum >= low; sum = sum - 1) {
            if (reachable[sum]) {
                best = sum;
                break;
            }
        }

        if (best < 0) {
            System.out.println("Разбить нельзя.");
            return;
        }

        // Восстанавливаем, какие камни попали в первую кучу.
        boolean[] used = new boolean[n];
        int sum = best;
        while (sum > 0) {
            int index = lastIndex[sum];
            used[index] = true;
            sum = sum - weights[index];
        }

        int sum1 = 0;
        int sum2 = 0;
        StringBuilder pile1 = new StringBuilder();
        StringBuilder pile2 = new StringBuilder();

        for (int i = 0; i < n; i++) {
            if (used[i]) {
                sum1 = sum1 + weights[i];
                pile1.append(weights[i]).append(' ');
            } else {
                sum2 = sum2 + weights[i];
                pile2.append(weights[i]).append(' ');
            }
        }

        System.out.println("Куча 1 (" + sum1 + "): " + pile1.toString().trim());
        System.out.println("Куча 2 (" + sum2 + "): " + pile2.toString().trim());

        int max = sum1 > sum2 ? sum1 : sum2;
        int min = sum1 < sum2 ? sum1 : sum2;
        double ratio = (double) max / min;
        System.out.println("Отношение весов: " + ratio);
    }
}
