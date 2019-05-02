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
            //get every symbol transitions to an alphabet
            Set<Character> alphabet = new HashSet<>();
            for (Node node : state)
            {
                alphabet.addAll(node.getTransitions().keySet());
            }

            for (char sym : alphabet)
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
                    DFA.getNode(index).addTransition(sym, addNode());
                }
                else
                {
                    DFA.getNode(index).addTransition(sym, DFA.getNode(DStates.indexOf(newState)));
                }
            }
        }
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
            T.addAll(T.get(i).getEpsilonTransitions());
        }
        return T;
    }

    private ArrayList<Node> move(ArrayList<Node> T, char sym)
    {
        ArrayList<Node> result = new ArrayList<>();

        for (Node node : T)
        {
            Node nextNode = node.getNode(sym);
            if (nextNode != null && !result.contains(nextNode))
            {
                result.add(nextNode);
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
