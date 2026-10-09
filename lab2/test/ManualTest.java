public class ManualTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(boolean condition, String testName) {
        if (condition) {
            System.out.println("OK: " + testName);
            passed = passed + 1;
        } else {
            System.out.println("FAIL: " + testName);
            failed = failed + 1;
        }
    }

    private static SparseMatrixCS build(int[][] dense) {
        SparseMatrixCS matrix = new SparseMatrixCS(dense.length, dense[0].length);
        for (int i = 0; i < dense.length; i = i + 1) {
            for (int j = 0; j < dense[i].length; j = j + 1) {
                matrix.set(i, j, dense[i][j]);
            }
        }
        return matrix;
    }

    public static void main(String[] args) {

        SparseMatrixCS first = build(new int[][]{{0, 5, 0}, {6, 0, 7}});
        check(first.nonZeroCount() == 3, "хранение только ненулевых");
        check(first.get(0, 0) == 0, "чтение отсутствующего элемента");
        check(first.get(0, 1) == 5, "чтение элемента");

        StringList result = new StringList();
        MatrixWalker.walkVariant1(first, result);
        boolean order = result.size() == 3
                && result.getAt(0).equals("a[0][1] = 5")
                && result.getAt(1).equals("a[1][0] = 6")
                && result.getAt(2).equals("a[1][2] = 7");
        check(order, "порядок обхода варианта 1");

        SparseMatrixCS empty = build(new int[][]{{0, 0}, {0, 0}});
        StringList emptyResult = new StringList();
        MatrixWalker.walkVariant1(empty, emptyResult);
        check(empty.nonZeroCount() == 0 && emptyResult.isEmpty(), "пустая матрица");

        SparseMatrixCS single = build(new int[][]{{0, 0, 0}, {0, 42, 0}});
        StringList singleResult = new StringList();
        MatrixWalker.walkVariant1(single, singleResult);
        check(singleResult.size() == 1
                && singleResult.getAt(0).equals("a[1][1] = 42"), "один элемент");

        SparseMatrixCS negative = build(new int[][]{{-3, 0}, {0, -1}});
        StringList negativeResult = new StringList();
        MatrixWalker.walkVariant1(negative, negativeResult);
        check(negativeResult.size() == 2
                && negativeResult.getAt(0).equals("a[0][0] = -3")
                && negativeResult.getAt(1).equals("a[1][1] = -1"), "отрицательные значения");

        try {
            check(Main.parseInt("-12") == -12, "разбор отрицательного числа");
        } catch (Exception error) {
            check(false, "разбор отрицательного числа");
        }

        System.out.println("Итого: passed=" + passed + " failed=" + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
