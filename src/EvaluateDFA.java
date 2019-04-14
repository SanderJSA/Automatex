public class EvaluateDFA {

    public static String evaluate(Graph DFA, String text)
    {
        for (int i = 0; i < text.length(); i++)
        {
            int end = selectionIsValid(DFA, text, i);
            if (end != i)
            {
                //[31m is red and [0m is reset
                text = text.substring(0, i) + "\u001B[31m" + text.substring(i, end) + "\u001B[0m" + text.substring(end);
                i += 9;
            }
        }

        return text;
    }

    private static int selectionIsValid(Graph DFA, String text, int start)
    {
        int i = start;

        Node node = DFA.getInitial();
        while ( i < text.length())
        {
            boolean changedState = false;
            for (Edge edge : node.getConnections())
            {
                if (text.charAt(i) == edge.getWeight().charAt(0))
                {
                    changedState = true;
                    node = edge.getEnd();
                    i++;
                    break;
                }


            }
            if (!changedState)
            {
                return (node.isAcceptState()) ? i : start;
            }
        }
        return (node.isAcceptState()) ? i : start;
    }
}
