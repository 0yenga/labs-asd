// Обход разреженной матрицы по варианту 1.
// Вариант 1: построчный обход слева направо, сверху вниз.
// Ненулевые элементы собираются в односвязный список результата.
public class MatrixWalker {
    // Обход матрицы и вывод всех ненулевых элементов в порядке варианта 1.
    // Каждый найденный элемент добавляется в список result в порядке обхода.
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
