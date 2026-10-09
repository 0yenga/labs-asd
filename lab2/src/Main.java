import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class Main {

    static int parseInt(String text) {
        if (text == null || text.length() == 0) {
            throw new IllegalArgumentException("Ожидалось целое число.");
        }
        int start = 0;
        boolean negative = false;
        if (text.charAt(0) == '-') {
            negative = true;
            start = 1;
        }
        if (start >= text.length()) {
            throw new IllegalArgumentException("Ожидалось целое число.");
        }
        int value = 0;
        for (int i = start; i < text.length(); i = i + 1) {
            char c = text.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("Ожидалось целое число, получено: " + text);
            }
            value = value * 10 + (c - '0');
        }
        if (negative) {
            value = -value;
        }
        return value;
    }

    static boolean isBlank(String line) {
        for (int i = 0; i < line.length(); i = i + 1) {
            char c = line.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n' && c != '\uFEFF') {
                return false;
            }
        }
        return true;
    }

    // BOM из Блокнота Windows считается разделителем.
    static void splitNumbers(String line, StringList tokens) {
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i = i + 1) {
            char c = line.charAt(i);
            boolean separator = c == ' ' || c == '\t' || c == ',' || c == ';' || c == '\uFEFF';
            if (separator) {
                if (current.length() > 0) {
                    tokens.append(current.toString());
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.append(current.toString());
        }
    }

    static class TokenReader {
        BufferedReader reader;
        StringList tokens;
        int position;

        TokenReader(BufferedReader inputReader) {
            this.reader = inputReader;
            this.tokens = new StringList();
            this.position = 0;
        }

        String nextToken() throws IOException {
            while (position >= tokens.size()) {
                String line = reader.readLine();
                if (line == null) {
                    return null;
                }
                if (isBlank(line)) {
                    continue;
                }
                splitNumbers(line, tokens);
            }
            String token = tokens.getAt(position);
            position = position + 1;
            return token;
        }
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            TokenReader input = new TokenReader(reader);
            String rowsToken = input.nextToken();
            String colsToken = input.nextToken();
            if (rowsToken == null || colsToken == null) {
                throw new IllegalArgumentException("Входные данные отсутствуют. Ожидались размеры матрицы N M.");
            }
            int rows = parseInt(rowsToken);
            int cols = parseInt(colsToken);
            if (rows <= 0 || cols <= 0) {
                throw new IllegalArgumentException("Размеры матрицы должны быть положительными.");
            }
            SparseMatrixCS matrix = new SparseMatrixCS(rows, cols);
            for (int i = 0; i < rows; i = i + 1) {
                for (int j = 0; j < cols; j = j + 1) {
                    String token = input.nextToken();
                    if (token == null) {
                        throw new IllegalArgumentException("Элементов матрицы меньше, чем N * M.");
                    }
                    int value = parseInt(token);
                    matrix.set(i, j, value);
                }
            }
            StringList result = new StringList();
            MatrixWalker.walkVariant1(matrix, result);
            System.out.println("Размер матрицы: " + rows + " x " + cols);
            System.out.println("Ненулевых элементов: " + matrix.nonZeroCount());
            System.out.println("Обход по варианту 1 (по строкам слева направо, сверху вниз):");
            if (result.isEmpty()) {
                System.out.println("нет");
            } else {
                ListNode current = result.getHead();
                while (current != null) {
                    System.out.println(current.data);
                    current = current.next;
                }
            }
        } catch (IOException error) {
            System.out.println("Ошибка: ошибка чтения входных данных.");
            System.exit(1);
        } catch (IllegalArgumentException error) {
            System.out.println("Ошибка: " + error.getMessage());
            System.exit(1);
        } catch (Exception error) {
            System.out.println("Ошибка: " + error.getMessage());
            System.exit(1);
        }
    }
}
