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
                i = end + 8;
            }
        }

        return text;
    }

    private static int selectionIsValid(Graph DFA, String text, int i) {
        int lastValid = i;
        Node node = DFA.getInitial();

        while (i < text.length()) {
            //Move to next node and character
            node = node.getNode(text.charAt(i));
            i++;

            if (node == null)
            {
                return lastValid;
            }

            if (node.isAcceptState())
            {
                lastValid = i;
            }

        }
        return lastValid;
    }
}
