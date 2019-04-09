import java.util.ArrayList;

public class Node {
    private int name;
    private ArrayList<Edge> connections;

    public Node()
    {
        this(0);
    }

    public Node(int name) {
        this.name = name;
        this.connections = new ArrayList<>();
    }


    public void addConnection(Node finish, String weight)
    {
        connections.add(new Edge(finish, weight));
    }

    //GETTER AND SETTER

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public ArrayList<Edge> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<Edge> connections) {
        this.connections = connections;
    }
}
