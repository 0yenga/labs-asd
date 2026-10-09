public class MatrixWalker {

    // Вариант 1: строки слева направо, сверху вниз.
    public static void walkVariant1(SparseMatrixCS matrix, StringList result) {
        for (int i = 0; i < matrix.rowCount(); i = i + 1) {
            for (int j = 0; j < matrix.colCount(); j = j + 1) {
                int value = matrix.get(i, j);
                if (value != 0) {
                    result.append("a[" + i + "][" + j + "] = " + value);
                }
            }
        }
    }
}
