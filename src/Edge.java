public class Edge {
    private Node end;
    private String weight;

    public Edge()
    {
        this(new Node(), "");
    }

    public Edge(Node end, String weight) {
        this.end = end;
        this.weight = (weight == null) ? "\" \"" : weight;
    }

    //GETTER AND SETTER

    public Node getEnd() {
        return end;
    }

    public void setEnd(Node end) {
        this.end = end;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}