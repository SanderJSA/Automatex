import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Graph {
    private String name;
    private ArrayList<Node> nodes;

    public Graph() {
        this("");
    }

    public Graph(String name) {
        this.name = name;
        nodes = new ArrayList<>();
    }

    public void saveGraph()
    {
        PrintWriter file = null;
        try
        {
            file = new PrintWriter("Output.dot");
        }
        catch (FileNotFoundException e)
        {
            System.out.println ("Error opening the file ");
            System.exit (0);
        }

        file.println("digraph " + name + " {");
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
