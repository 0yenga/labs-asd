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

    private static PrecedenceAnalyzer build(String[][] pairs) {
        PrecedenceAnalyzer analyzer = new PrecedenceAnalyzer();
        for (int i = 0; i < pairs.length; i = i + 1) {
            analyzer.addPair(pairs[i][0], pairs[i][1]);
        }
        return analyzer;
    }

    public static void main(String[] args) {

        String[][] linear = new String[][]{{"a", "b"}, {"b", "c"}};
        PrecedenceAnalyzer.Result first = build(linear).analyze();
        check(first.complete && !first.contradictory, "полный порядок");

        String[][] direct = new String[][]{{"a", "b"}, {"b", "a"}};
        PrecedenceAnalyzer.Result second = build(direct).analyze();
        check(!second.complete && second.contradictory, "прямое противоречие");

        String[][] indirect = new String[][]{{"a", "b"}, {"b", "c"}, {"c", "a"}};
        PrecedenceAnalyzer.Result third = build(indirect).analyze();
        check(!third.complete && third.contradictory, "косвенное противоречие");

        String[][] split = new String[][]{{"a", "b"}, {"c", "d"}};
        PrecedenceAnalyzer.Result fourth = build(split).analyze();
        check(!fourth.complete && !fourth.contradictory, "неполный порядок");

        String[][] empty = new String[0][0];
        PrecedenceAnalyzer.Result fifth = build(empty).analyze();
        check(fifth.complete && !fifth.contradictory && fifth.vertices.size() == 0, "пустой набор пар");

        try {
            String[] parsed = Main.parsePair("(a, b)", 2);
            check(parsed[0].equals("a") && parsed[1].equals("b"), "разбор со скобками");
        } catch (Exception error) {
            check(false, "разбор со скобками");
        }

        try {
            int count = Main.parseCount("  3  ");
            check(count == 3, "разбор числа N");
        } catch (Exception error) {
            check(false, "разбор числа N");
        }

        System.out.println("Итого: passed=" + passed + " failed=" + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
