import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// Лабораторная работа 1, задача 1: списки, стеки, очереди.
// Программа читает пары символов и проверяет полноту
// и противоречивость порядка предшествования.
// Все списки и стек реализованы вручную на узлах.
public class Main {
    // Разбор неотрицательного целого числа вручную, без готовых функций.
    static int parseCount(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Первая строка должна содержать неотрицательное целое число N.");
        }
        int start = 0;
        int end = line.length();
        while (start < end) {
            char c = line.charAt(start);
            if (c == ' ' || c == '\t') {
                start = start + 1;
            } else {
                break;
            }
        }
        while (end > start) {
            char c = line.charAt(end - 1);
            if (c == ' ' || c == '\t') {
                end = end - 1;
            } else {
                break;
            }
        }
        if (start >= end) {
            throw new IllegalArgumentException("Первая строка должна содержать неотрицательное целое число N.");
        }
        int value = 0;
        for (int i = start; i < end; i = i + 1) {
            char c = line.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException("Первая строка должна содержать неотрицательное целое число N.");
            }
            value = value * 10 + (c - '0');
        }
        return value;
    }

    // Разбор одной пары из строки, допускаются скобки, запятые и точки с запятой.
    // Пример допустимых записей: "a b", "(a, b)", "(a;b)".
    static String[] parsePair(String line, int lineNumber) {
        String first = "";
        String second = "";
        StringBuilder current = new StringBuilder();
        int tokenCount = 0;
        String firstToken = null;
        String secondToken = null;
        for (int i = 0; i < line.length(); i = i + 1) {
            char c = line.charAt(i);
            boolean separator = c == ' ' || c == '\t' || c == '(' || c == ')'
                    || c == ',' || c == ';';
            if (separator) {
                if (current.length() > 0) {
                    if (tokenCount == 0) {
                        firstToken = current.toString();
                    } else if (tokenCount == 1) {
                        secondToken = current.toString();
                    }
                    tokenCount = tokenCount + 1;
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            if (tokenCount == 0) {
                firstToken = current.toString();
            } else if (tokenCount == 1) {
                secondToken = current.toString();
            }
            tokenCount = tokenCount + 1;
        }
        if (tokenCount != 2 || firstToken == null || secondToken == null) {
            throw new IllegalArgumentException("Некорректная пара в строке " + lineNumber + ".");
        }
        first = firstToken;
        second = secondToken;
        String[] pair = new String[2];
        pair[0] = first;
        pair[1] = second;
        return pair;
    }

    // Проверка, что строка пустая (только пробельные символы).
    static boolean isBlank(String line) {
        for (int i = 0; i < line.length(); i = i + 1) {
            char c = line.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    // Соединение списка символов через запятую для вывода.
    static String joinNames(StringList names) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < names.size(); i = i + 1) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(names.getAt(i));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            String line = reader.readLine();
            while (line != null && isBlank(line)) {
                line = reader.readLine();
            }
            if (line == null) {
                throw new IllegalArgumentException("Входные данные отсутствуют.");
            }
            int pairCount = parseCount(line);
            PrecedenceAnalyzer analyzer = new PrecedenceAnalyzer();
            int readPairs = 0;
            int lineNumber = 1;
            while (readPairs < pairCount) {
                String pairLine = reader.readLine();
                lineNumber = lineNumber + 1;
                if (pairLine == null) {
                    throw new IllegalArgumentException("Количество пар во входных данных меньше N.");
                }
                if (isBlank(pairLine)) {
                    continue;
                }
                String[] pair = parsePair(pairLine, lineNumber);
                analyzer.addPair(pair[0], pair[1]);
                readPairs = readPairs + 1;
            }
            PrecedenceAnalyzer.Result result = analyzer.analyze();
            StringList names = result.vertices;
            String used = names.size() == 0 ? "нет" : joinNames(names);
            System.out.println("Количество пар: " + readPairs);
            System.out.println("Использованные символы: " + used);
            System.out.println("Порядок является полным: " + (result.complete ? "да" : "нет"));
            System.out.println("Порядок является противоречивым: " + (result.contradictory ? "да" : "нет"));
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
