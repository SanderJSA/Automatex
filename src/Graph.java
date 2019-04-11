import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Graph {
    private String name;
    private ArrayList<Node> nodes;
    private Node initial;
    private Node finish;

    public Graph() {
        this("");
    }

    public Graph(String name) {
        this.name = name;
        nodes = new ArrayList<>();
        initial = null;
        finish = null;
    }

    public void saveGraph()
    {
        PrintWriter file = null;
        try
        {
            file = new PrintWriter(name + ".dot");
        }
        catch (FileNotFoundException e)
        {
            System.out.println ("Error opening the file ");
            System.exit (0);
        }

        file.println("digraph " + name + " {");
        file.println("rankdir=LR");
        file.println("node [shape = point, color=white, fontcolor=white]; start");
        file.println("node [shape = doublecircle, color=black, fontcolor=black]; " + finish.getName() + ";");
        file.println("node [shape = circle];");
        file.println("start -> " + initial.getName());
        for (Node node : nodes)
        {
            for (Edge edge : node.getConnections())
            {
                file.println(node.getName() + " -> " + edge.getEnd().getName() + " [label = " + edge.getWeight() + " ]");
            }
        }
        file.println("}");

        file.close();
    }

    public Node getNode(int index)
    {
        return nodes.get(index);
    }

    //GETTER AND SETTER


    public Node getInitial() {
        return initial;
    }

    public void setInitial(Node initial) {
        this.initial = initial;
    }

    public Node getFinish() {
        return finish;
    }

    public void setFinish(Node finish) {
        this.finish = finish;
    }

    public void addNode(Node node)
    {
        nodes.add(node);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
}
