public class VertexNode {

    public String name;

    public AdjNode edges;

    public VertexNode next;

    public VertexNode(String vertexName) {
        this.name = vertexName;
        this.edges = null;
        this.next = null;
    }

    public void addEdge(String targetName) {
        AdjNode current = edges;
        while (current != null) {
            if (current.target.equals(targetName)) {
                return;
            }
            current = current.next;
        }
        AdjNode created = new AdjNode(targetName);
        created.next = edges;
        edges = created;
    }
}
