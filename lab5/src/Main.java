import java.util.Scanner;

public class Main {

    // Оставляет только цифры, разрешая разделители "-" и пробел.
    static String normalizeSnils(String raw) {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c >= '0' && c <= '9') {
                digits.append(c);
            } else if (c != '-' && c != ' ') {
                return null;
            }
        }
        if (digits.length() != 11) {
            return null;
        }
        return digits.toString();
    }

    // Возвращает начало строки до 11-й цифры включительно.
    // Это позволяет вводить СНИЛС как "123-456-789 00" и затем ФИО.
    static String keyPrefix(String text) {
        int digits = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '0' && c <= '9') {
                digits = digits + 1;
                if (digits == 11) {
                    if (i + 1 < text.length()) {
                        char next = text.charAt(i + 1);
                        if (next >= '0' && next <= '9') {
                            return null;
                        }
                    }
                    return text.substring(0, i + 1);
                }
            } else if (c != '-' && c != ' ') {
                return null;
            }
        }
        return null;
    }

    static void printHelp() {
        System.out.println("Команды:");
        System.out.println("  insert <СНИЛС> <ФИО>  добавить или обновить запись");
        System.out.println("  search <СНИЛС>        найти запись");
        System.out.println("  delete <СНИЛС>        удалить запись");
        System.out.println("  print                 напечатать таблицу");
        System.out.println("  help                  показать команды");
        System.out.println("  exit                  выход");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashTable table = new HashTable();
        printHelp();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // BOM из Блокнота Windows не должен попадать в имя команды.
            if (!line.isEmpty() && line.charAt(0) == '\uFEFF') {
                line = line.substring(1);
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            int space = line.indexOf(' ');
            String command = space < 0 ? line : line.substring(0, space);
            String rest = space < 0 ? "" : line.substring(space + 1).trim();

            if (command.equals("insert")) {
                String rawKey = keyPrefix(rest);
                if (rawKey == null) {
                    System.out.println("Ошибка: СНИЛС должен содержать 11 цифр.");
                    continue;
                }
                String info = rest.substring(rawKey.length()).trim();
                String key = normalizeSnils(rawKey);
                if (info.isEmpty()) {
                    info = "нет данных";
                }
                boolean added = table.insert(key, info);
                if (added) {
                    System.out.println("Добавлено: " + key + " (" + info + ")");
                } else {
                    System.out.println("Обновлено: " + key + " (" + info + ")");
                }
                System.out.println("Элементов: " + table.size() + ", размер таблицы: " + table.capacity());
            } else if (command.equals("search")) {
                String rawKey = keyPrefix(rest);
                if (rawKey == null) {
                    System.out.println("Ошибка: СНИЛС должен содержать 11 цифр.");
                    continue;
                }
                String key = normalizeSnils(rawKey);
                if (key == null) {
                    System.out.println("Ошибка: СНИЛС должен содержать 11 цифр.");
                    continue;
                }
                String info = table.find(key);
                if (info == null) {
                    System.out.println("Не найдено: " + key);
                } else {
                    System.out.println("Найдено: " + key + " (" + info + ")");
                }
            } else if (command.equals("delete")) {
                String rawKey = keyPrefix(rest);
                if (rawKey == null) {
                    System.out.println("Ошибка: СНИЛС должен содержать 11 цифр.");
                    continue;
                }
                String key = normalizeSnils(rawKey);
                if (key == null) {
                    System.out.println("Ошибка: СНИЛС должен содержать 11 цифр.");
                    continue;
                }
                if (table.delete(key)) {
                    System.out.println("Удалено: " + key);
                } else {
                    System.out.println("Не найдено: " + key);
                }
                System.out.println("Элементов: " + table.size());
            } else if (command.equals("print")) {
                table.print();
                System.out.println("Элементов: " + table.size() + ", размер таблицы: " + table.capacity());
            } else if (command.equals("help")) {
                printHelp();
            } else if (command.equals("exit")) {
                break;
            } else {
                System.out.println("Неизвестная команда: " + command);
                printHelp();
            }
        }
    }
}
