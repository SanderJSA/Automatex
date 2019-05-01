import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Graph {
    private String name;
    private ArrayList<Node> nodes;
    private Node initial;
    private Node finish;
    private Set<Character> alphabet;

    public Graph() {
        this("");
    }

    public Graph(String name) {
        this.name = name;
        nodes = new ArrayList<>();
        initial = null;
        finish = null;
        alphabet = new HashSet<>();
    }

    public void saveGraph()
    {
        //Open or Create file
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

        //Write to file
        file.println("digraph " + name + " {");
        file.println("rankdir=LR");
        file.println("node [shape = point, color=white, fontcolor=white]; start");
        file.println("node [shape = doublecircle, color=black, fontcolor=black];");
        for (Node node : nodes)
        {
            if (node.isAcceptState())
                file.println(node.getName());
        }
        file.println("node [shape = circle];");
        file.println("start -> " + initial.getName());
        for (Node node : nodes)
        {
            for (Edge edge : node.getConnections())
            {
                String label = (edge.getWeight() == null) ? "" : edge.getWeight();
                file.println(node.getName() + " -> " + edge.getEnd().getName() + " [label = \"" + label + "\" ]");
            }
        }
        file.println("}");

        file.close();

        //Generate image from .dot file
        try
        {
            Runtime.getRuntime().exec("dot " + name + ".dot -Tpng -o " + name + ".png");
        }
        catch (IOException e)
        {
            System.out.println("Could not generate picture from .dot file, is Graphviz installed ?");
        }
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

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public void addSymbol(char sym) {
        alphabet.add(sym);
    }
}
