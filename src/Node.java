import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {
    private int name;
    private Map<Character, Node> transitions;
    private ArrayList<Node> epsilonTransitions;
    private boolean acceptState;

    public Node(int name) {
        this.name = name;
        this.transitions = new HashMap<>();
        this.epsilonTransitions = new ArrayList<>();
        this.acceptState = false;
    }

    public void addEpsilon(Node finish)
    {
        epsilonTransitions.add(finish);
    }

    public void addTransition(Character weight, Node finish)
    {
        transitions.put(weight, finish);
    }

    //GETTER AND SETTER

    public int getName() {
        return name;
    }

    public Node getNode(Character weight)
    {
        return transitions.get(weight);
    }

    public ArrayList<Node> getEpsilonTransitions()
    {
        return epsilonTransitions;
    }

    public Map<Character, Node> getTransitions()
    {
        return transitions;
    }

    public boolean isAcceptState() {
        return acceptState;
    }

    public void setAcceptState(boolean acceptState) {
        this.acceptState = acceptState;
    }
}
