import java.util.Scanner;

public class Main {

    // Типы клеток поля.
    static final int BLACK = 0;
    static final int CLUE = 1;
    static final int WHITE = 2;

    static int rows;
    static int cols;

    static int[][] type;
    static int[][] value;        // цифра в белой клетке, 0 — клетка ещё пуста
    static int[][] clueRowSum;   // сумма по строке для клетки-подсказки
    static int[][] clueColSum;   // сумма по столбцу для клетки-подсказки

    // Для каждой белой клетки: границы её горизонтального и вертикального отрезка
    // и суммы, которые должны получиться на этих отрезках.
    static int[][] rowFirst;
    static int[][] rowLast;
    static int[][] colFirst;
    static int[][] colLast;
    static int[][] rowTarget;
    static int[][] colTarget;

    static int emptyRow;
    static int emptyCol;

    static int solutionCount = 0;
    static final int MAX_SOLUTIONS = 2; // хватит, чтобы отличить одно решение от нескольких

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Какуро: рекурсивный решатель.");
        System.out.println("Введите количество строк и столбцов поля.");
        if (!scanner.hasNext()) {
            System.out.println("Ошибка: не найдены размеры поля.");
            return;
        }
        String rowsText = stripBom(scanner.next());
        if (!scanner.hasNext()) {
            System.out.println("Ошибка: не найдено количество столбцов.");
            return;
        }
        String colsText = stripBom(scanner.next());
        try {
            rows = Integer.parseInt(rowsText);
            cols = Integer.parseInt(colsText);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: размеры поля должны быть целыми числами.");
            return;
        }
        if (rows <= 0 || cols <= 0) {
            System.out.println("Ошибка: размеры поля должны быть положительными.");
            return;
        }

        type = new int[rows][cols];
        value = new int[rows][cols];
        clueRowSum = new int[rows][cols];
        clueColSum = new int[rows][cols];

        System.out.println("Введите поле: # — чёрная клетка, . — пустая клетка,");
        System.out.println("r/c — подсказка (r — сумма по строке, c — сумма по столбцу, 0 — суммы нет).");

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!scanner.hasNext()) {
                    System.out.println("Ошибка: поле введено не полностью.");
                    return;
                }
                if (!parseCell(r, c, scanner.next())) {
                    return;
                }
            }
        }

        if (!prepare()) {
            return;
        }

        System.out.println();
        System.out.println("Исходное поле:");
        printGrid(false);
        System.out.println();
        System.out.println("Поиск решения...");
        solve();
        System.out.println();

        if (solutionCount == 0) {
            System.out.println("Решение не найдено.");
        } else if (solutionCount == 1) {
            System.out.println("Найдено единственное решение.");
        } else {
            System.out.println("Найдено несколько решений (показано первое).");
        }
    }

    // BOM из Блокнота Windows не должен мешать разбору первого токена.
    static String stripBom(String text) {
        if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {
            return text.substring(1);
        }
        return text;
    }

    // Разбор одной клетки поля.
    static boolean parseCell(int r, int c, String token) {
        token = stripBom(token);
        if (token.equals("#")) {
            type[r][c] = BLACK;
            return true;
        }
        if (token.equals(".")) {
            type[r][c] = WHITE;
            return true;
        }

        int slash = token.indexOf('/');
        if (slash <= 0 || slash == token.length() - 1) {
            System.out.println("Ошибка: клетка (" + (r + 1) + "," + (c + 1)
                    + ") должна быть #, . или r/c.");
            return false;
        }

        int rowSum = parseSum(token.substring(0, slash));
        int colSum = parseSum(token.substring(slash + 1));
        if (rowSum < 0 || colSum < 0) {
            System.out.println("Ошибка: неверная подсказка в клетке (" + (r + 1) + "," + (c + 1) + ").");
            return false;
        }

        type[r][c] = CLUE;
        clueRowSum[r][c] = rowSum;
        clueColSum[r][c] = colSum;
        return true;
    }

    // 0 и "-" означают, что суммы нет. Отрицательное значение — ошибка разбора.
    static int parseSum(String text) {
        if (text.equals("0") || text.equals("-")) {
            return 0;
        }
        try {
            int number = Integer.parseInt(text);
            if (number < 1 || number > 45) {
                return -1;
            }
            return number;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Проверка поля и подготовка границ отрезков для каждой белой клетки.
    static boolean prepare() {
        rowFirst = new int[rows][cols];
        rowLast = new int[rows][cols];
        colFirst = new int[rows][cols];
        colLast = new int[rows][cols];
        rowTarget = new int[rows][cols];
        colTarget = new int[rows][cols];

        // У каждой подсказки проверяем, что направление с суммой действительно
        // имеет белые клетки, а направление без суммы — не имеет.
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (type[r][c] != CLUE) {
                    continue;
                }
                boolean hasRight = c + 1 < cols && type[r][c + 1] == WHITE;
                boolean hasDown = r + 1 < rows && type[r + 1][c] == WHITE;

                if (hasRight && clueRowSum[r][c] == 0) {
                    System.out.println("Ошибка: у подсказки (" + (r + 1) + "," + (c + 1)
                            + ") нет суммы по строке.");
                    return false;
                }
                if (!hasRight && clueRowSum[r][c] != 0) {
                    System.out.println("Ошибка: подсказка (" + (r + 1) + "," + (c + 1)
                            + ") задаёт сумму по строке, но справа нет белых клеток.");
                    return false;
                }
                if (hasDown && clueColSum[r][c] == 0) {
                    System.out.println("Ошибка: у подсказки (" + (r + 1) + "," + (c + 1)
                            + ") нет суммы по столбцу.");
                    return false;
                }
                if (!hasDown && clueColSum[r][c] != 0) {
                    System.out.println("Ошибка: подсказка (" + (r + 1) + "," + (c + 1)
                            + ") задаёт сумму по столбцу, но снизу нет белых клеток.");
                    return false;
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (type[r][c] != WHITE) {
                    continue;
                }

                int first = c;
                while (first > 0 && type[r][first - 1] == WHITE) {
                    first--;
                }
                int last = c;
                while (last + 1 < cols && type[r][last + 1] == WHITE) {
                    last++;
                }

                int top = r;
                while (top > 0 && type[top - 1][c] == WHITE) {
                    top--;
                }
                int bottom = r;
                while (bottom + 1 < rows && type[bottom + 1][c] == WHITE) {
                    bottom++;
                }

                if (first == 0 || type[r][first - 1] != CLUE || clueRowSum[r][first - 1] == 0) {
                    System.out.println("Ошибка: у белой клетки (" + (r + 1) + "," + (c + 1)
                            + ") нет подсказки слева.");
                    return false;
                }
                if (top == 0 || type[top - 1][c] != CLUE || clueColSum[top - 1][c] == 0) {
                    System.out.println("Ошибка: у белой клетки (" + (r + 1) + "," + (c + 1)
                            + ") нет подсказки сверху.");
                    return false;
                }

                rowFirst[r][c] = first;
                rowLast[r][c] = last;
                colFirst[r][c] = top;
                colLast[r][c] = bottom;
                rowTarget[r][c] = clueRowSum[r][first - 1];
                colTarget[r][c] = clueColSum[top - 1][c];

                if (!checkRange(last - first + 1, rowTarget[r][c], r, c, "строке")) {
                    return false;
                }
                if (!checkRange(bottom - top + 1, colTarget[r][c], r, c, "столбцу")) {
                    return false;
                }
            }
        }

        return true;
    }

    // Для отрезка длины length проверяем, что сумму можно набрать
    // различными цифрами от 1 до 9.
    static boolean checkRange(int length, int target, int r, int c, String direction) {
        if (length > 9) {
            System.out.println("Ошибка: в " + direction + " клетки (" + (r + 1) + "," + (c + 1)
                    + ") больше 9 клеток.");
            return false;
        }
        int min = length * (length + 1) / 2;
        int max = length * (19 - length) / 2;
        if (target < min || target > max) {
            System.out.println("Ошибка: сумму " + target + " нельзя набрать в " + direction
                    + " клетки (" + (r + 1) + "," + (c + 1) + ") из " + length + " клеток.");
            return false;
        }
        return true;
    }

    // Рекурсивный поиск решения. На каждом шаге берём первую пустую белую клетку
    // и пробуем поставить в неё цифры от 1 до 9.
    static void solve() {
        if (!findEmpty()) {
            if (checkAll()) {
                solutionCount++;
                if (solutionCount == 1) {
                    System.out.println("Решение:");
                    printGrid(true);
                }
            }
            return;
        }

        int r = emptyRow;
        int c = emptyCol;

        for (int digit = 1; digit <= 9; digit++) {
            if (canPlace(r, c, digit)) {
                value[r][c] = digit;
                solve();
                value[r][c] = 0;

                if (solutionCount >= MAX_SOLUTIONS) {
                    return;
                }
            }
        }
    }

    static boolean findEmpty() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (type[r][c] == WHITE && value[r][c] == 0) {
                    emptyRow = r;
                    emptyCol = c;
                    return true;
                }
            }
        }
        return false;
    }

    // Проверяем, можно ли поставить digit в клетку (r,c):
    // цифра не должна повторяться в строке и столбце, а частичная сумма
    // не должна превышать заданную.
    static boolean canPlace(int r, int c, int digit) {
        int sum = digit;
        int empty = 0;
        for (int j = rowFirst[r][c]; j <= rowLast[r][c]; j++) {
            if (j == c) {
                continue;
            }
            if (value[r][j] == digit) {
                return false;
            }
            sum += value[r][j];
            if (value[r][j] == 0) {
                empty++;
            }
        }
        if (sum > rowTarget[r][c]) {
            return false;
        }
        if (empty == 0) {
            if (sum != rowTarget[r][c]) {
                return false;
            }
        } else if (!canFinish(sum, empty, rowTarget[r][c])) {
            return false;
        }

        sum = digit;
        empty = 0;
        for (int i = colFirst[r][c]; i <= colLast[r][c]; i++) {
            if (i == r) {
                continue;
            }
            if (value[i][c] == digit) {
                return false;
            }
            sum += value[i][c];
            if (value[i][c] == 0) {
                empty++;
            }
        }
        if (sum > colTarget[r][c]) {
            return false;
        }
        if (empty == 0) {
            return sum == colTarget[r][c];
        }
        return canFinish(sum, empty, colTarget[r][c]);
    }

    // Оставшиеся empty клеток должны в сумме дать target - sum.
    // Проверяем минимально и максимально возможные суммы.
    static boolean canFinish(int sum, int empty, int target) {
        int rest = target - sum;
        int min = empty * (empty + 1) / 2;
        int max = empty * (19 - empty) / 2;
        return rest >= min && rest <= max;
    }

    // Полная проверка готового поля: все суммы и отсутствие повторов.
    static boolean checkAll() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (type[r][c] != CLUE) {
                    continue;
                }

                if (clueRowSum[r][c] > 0) {
                    int sum = 0;
                    boolean[] used = new boolean[10];
                    for (int j = c + 1; j < cols && type[r][j] == WHITE; j++) {
                        int digit = value[r][j];
                        if (digit == 0 || used[digit]) {
                            return false;
                        }
                        used[digit] = true;
                        sum += digit;
                    }
                    if (sum != clueRowSum[r][c]) {
                        return false;
                    }
                }

                if (clueColSum[r][c] > 0) {
                    int sum = 0;
                    boolean[] used = new boolean[10];
                    for (int i = r + 1; i < rows && type[i][c] == WHITE; i++) {
                        int digit = value[i][c];
                        if (digit == 0 || used[digit]) {
                            return false;
                        }
                        used[digit] = true;
                        sum += digit;
                    }
                    if (sum != clueColSum[r][c]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static void printGrid(boolean showSolution) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String text;
                if (type[r][c] == BLACK) {
                    text = "#";
                } else if (type[r][c] == CLUE) {
                    text = sumText(clueRowSum[r][c]) + "/" + sumText(clueColSum[r][c]);
                } else if (showSolution && value[r][c] > 0) {
                    text = String.valueOf(value[r][c]);
                } else {
                    text = ".";
                }
                System.out.printf("%6s", text);
            }
            System.out.println();
        }
    }

    static String sumText(int sum) {
        if (sum == 0) {
            return "-";
        }
        return String.valueOf(sum);
    }
}
