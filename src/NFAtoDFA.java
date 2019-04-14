import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NFAtoDFA {
    private Graph NFA;
    private Graph DFA;

    public NFAtoDFA(Graph NFA)
    {
        this.NFA = NFA;
        DFA = new Graph("DFA");
    }

    public static Graph convertToDFA(Graph NFA)
    {
        NFAtoDFA converter = new NFAtoDFA(NFA);

        ArrayList<Node> start = new ArrayList<>();
        start.add(converter.NFA.getInitial());

        converter.convert(converter.epsilonClosure(start));
        converter.DFA.setInitial(converter.DFA.getNode(0));

        return converter.DFA;



        /*
        while there are any unmarked states in D-States
{
  mark T
  for each input symbol a
  {
    U = EpsilonClosure(Move(T, a));
    if U is not in D-States
    {
      add U as an unmarked state to D-States
    }
    DTran[T,a] = U
  }
}
         */
    }

    private void convert(ArrayList<Node> start)
    {
        ArrayList<ArrayList<Node>> DStates = new ArrayList<>();
        ArrayList<Boolean> marked = new ArrayList<>();

        DStates.add(start);
        marked.add(false);
        addNode();

        while(marked.contains(false))
        {
            int index = marked.indexOf(false);
            ArrayList<Node> state = DStates.get(index);
            if (setContainsFinish(state))
                DFA.getNode(index).setAcceptState(true);

            marked.set(index, true);
            for (char sym : NFA.getAlphabet())
            {
                ArrayList<Node> newState = epsilonClosure(move(state, sym));
                if (newState.size()==0)
                {
                    continue;
                }

                if (!DStates.contains(newState))
                {
                    DStates.add(newState);
                    marked.add(false);
                    DFA.getNode(index).addConnection(addNode(), String.valueOf(sym));
                }
                else
                {
                    DFA.getNode(index).addConnection(DFA.getNode(DStates.indexOf(newState)), String.valueOf(sym));
                }
            }
        }
    }

    private Node nextNode(ArrayList<Node> T)
    {
        Node node = addNode();
        if (setContainsFinish(T))
            node.setAcceptState(true);

        for(char sym : NFA.getAlphabet())
        {
            ArrayList<Node> S = move(T, sym);
            S = epsilonClosure(S);
            if (!S.isEmpty())
            {
                if (S.equals(T))
                {
                    node.addConnection(node, String.valueOf(sym));
                }
                else
                {
                    node.addConnection(nextNode(S), String.valueOf(sym));
                }
            }
        }
        return node;
    }

    private boolean setContainsFinish(ArrayList<Node> T)
    {
        for (Node node : T)
        {
            if (node.isAcceptState())
                return true;
        }
        return false;
    }

    private ArrayList<Node> epsilonClosure(ArrayList<Node> T)
    {
        for (int i = 0; i < T.size(); i++)
        {
            for (Edge edge : T.get(i).getConnections())
            {
                if (edge.getWeight() == null)
                {
                    if (!T.contains(edge.getEnd()))
                        T.add(edge.getEnd());
                }
            }
        }
        return T;
    }

    private ArrayList<Node> move(ArrayList<Node> T, char sym)
    {
        ArrayList<Node> result = new ArrayList<>();

        for (Node node : T)
        {
            for (Edge edge : node.getConnections())
            {
                if (edge.getWeight() != null && edge.getWeight().charAt(0) == sym)
                {
                    if (!result.contains(edge.getEnd()))
                        result.add(edge.getEnd());
                }
            }
        }
        return result;
    }

    private Node addNode()
    {
        Node state = new Node(DFA.getNodes().size());
        DFA.addNode(state);
        return state;
    }
}
