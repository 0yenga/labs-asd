// Проверка порядка предшествования на ориентированном графе.
// Пары символов образуют дуги графа, символы являются вершинами.
// Полнота означает достижимость между любой парой вершин.
// Противоречивость означает наличие цикла (вершина достижима сама из себя).
public class PrecedenceAnalyzer {
    // Результат проверки порядка.
    public static class Result {
        // Признак полного порядка.
        public boolean complete;
        // Признак противоречивого порядка.
        public boolean contradictory;
        // Список использованных символов в порядке появления.
        public StringList vertices;

        Result(boolean completeValue, boolean contradictoryValue, StringList vertexList) {
            this.complete = completeValue;
            this.contradictory = contradictoryValue;
            this.vertices = vertexList;
        }
    }

    // Список всех вершин графа.
    private VertexList vertices;

    // Создание пустого анализатора.
    public PrecedenceAnalyzer() {
        this.vertices = new VertexList();
    }

    // Добавление пары предшествования (before предшествует after).
    public void addPair(String before, String after) {
        VertexNode from = vertices.getOrCreate(before);
        vertices.getOrCreate(after);
        from.addEdge(after);
    }

    // Список вершин в порядке первого появления.
    public StringList vertexNames() {
        StringList names = new StringList();
        VertexNode current = vertices.getHead();
        while (current != null) {
            names.append(current.name);
            current = current.next;
        }
        return names;
    }

    // Проверка достижимости цели из начала поиском в глубину.
    // Обход выполняется вручную с помощью стека на связном списке.
    private boolean reachable(String from, String to) {
        if (from.equals(to)) {
            return reachesItself(from);
        }
        StringList visited = new StringList();
        LinkedStack stack = new LinkedStack();
        stack.push(from);
        while (!stack.isEmpty()) {
            String currentName = stack.pop();
            if (currentName == null) {
                continue;
            }
            if (currentName.equals(to)) {
                return true;
            }
            if (visited.contains(currentName)) {
                continue;
            }
            visited.append(currentName);
            VertexNode currentVertex = vertices.find(currentName);
            if (currentVertex == null) {
                continue;
            }
            AdjNode edge = currentVertex.edges;
            while (edge != null) {
                if (!visited.contains(edge.target)) {
                    stack.push(edge.target);
                }
                edge = edge.next;
            }
        }
        return false;
    }

    // Проверка, достижима ли вершина сама из себя (наличие цикла).
    private boolean reachesItself(String start) {
        StringList visited = new StringList();
        LinkedStack stack = new LinkedStack();
        VertexNode startVertex = vertices.find(start);
        if (startVertex == null) {
            return false;
        }
        AdjNode edge = startVertex.edges;
        while (edge != null) {
            stack.push(edge.target);
            edge = edge.next;
        }
        while (!stack.isEmpty()) {
            String currentName = stack.pop();
            if (currentName == null) {
                continue;
            }
            if (currentName.equals(start)) {
                return true;
            }
            if (visited.contains(currentName)) {
                continue;
            }
            visited.append(currentName);
            VertexNode currentVertex = vertices.find(currentName);
            if (currentVertex == null) {
                continue;
            }
            AdjNode next = currentVertex.edges;
            while (next != null) {
                if (!visited.contains(next.target)) {
                    stack.push(next.target);
                }
                next = next.next;
            }
        }
        return false;
    }

    // Основная проверка: сначала ищется противоречие, затем полнота.
    public Result analyze() {
        StringList names = vertexNames();
        int size = names.size();

        // Поиск цикла: вершина достижима сама из себя.
        for (int i = 0; i < size; i = i + 1) {
            String vertex = names.getAt(i);
            if (reachesItself(vertex)) {
                return new Result(false, true, names);
            }
        }

        // Проверка полноты: каждая пара вершин связана достижимостью.
        for (int i = 0; i < size; i = i + 1) {
            for (int j = i + 1; j < size; j = j + 1) {
                String first = names.getAt(i);
                String second = names.getAt(j);
                boolean ordered = reachable(first, second) || reachable(second, first);
                if (!ordered) {
                    return new Result(false, false, names);
                }
            }
        }

        return new Result(true, false, names);
    }
}
