import java.util.Scanner;

public class Automatex {

    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);

        boolean stop = false;
        while (!stop) {

            System.out.println("Please input your regular expression : ");
            String regex = input.nextLine();

            Graph NFA = RegextoNFA.regexToNDFA(regex);
            NFA.saveGraph();

            Graph DFA = NFAtoDFA.convertToDFA(NFA);
            DFA.saveGraph();

            System.out.println("Please enter your string");
            String text = input.nextLine();
            String result = EvaluateDFA.evaluate(DFA, text);
            System.out.println(result);

            System.out.println("Press 0 if you want to stop");
            if (input.nextLine().equals("0"))
            {
                stop = true;
            }
        }

        input.close();
    }
}
