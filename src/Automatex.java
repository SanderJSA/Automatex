import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Automatex {


    private static Graph generateDFA(String regex, boolean debug)
    {
        Graph NFA = RegextoNFA.regexToNDFA(regex, debug);
        Graph DFA = NFAtoDFA.convertToDFA(NFA);

        if (debug)
        {
            NFA.saveGraph();
            DFA.saveGraph();
        }

        return DFA;
    }

    private static String readFile(String filename)
    {
        String content = "";
        try
        {
            content = new String ( Files.readAllBytes(Paths.get(filename)));
        }
        catch (IOException e)
        {
            System.out.println("Could not open file " + filename);
        }
        return content;
    }

    public static void main(String[] args)
    {
        //Initialization
        Scanner input = new Scanner(System.in);
        boolean debug = false;
        String regex = "";
        Graph DFA = generateDFA(regex, debug);
        System.out.println("+-----------------------------------------------------+");
        System.out.println("| Welcome to Automatex, a regular expression matcher. |");
        System.out.println("+-----------------------------------------------------+");

        //Main loop
        boolean stop = false;
        while (!stop)
        {

            //Main menu
            System.out.println("\n+-------------------------------------------+");
            System.out.println("| Please enter one of the following number: |");
            System.out.println("+-------------------------------------------+");
            System.out.println("| 0 to input a new regular expression       |");
            System.out.println("| 1 to evaluate a given string              |");
            System.out.println("| 2 to evaluate the contents of a file      |");
            System.out.println("| 3 to toggle Debug mode                    |");
            System.out.println("| 4 to exit the program                     |");
            System.out.println("+-------------------------------------------+");
            System.out.println("| Your current regular expression is:       |");
            System.out.println(String.format("| %-42s|", regex));
            System.out.println("+-------------------------------------------+");

            switch (input.nextLine().charAt(0))
            {
                case '0':
                    System.out.println("\n\nPlease input your Regex:");
                    regex = input.nextLine();
                    DFA = generateDFA(regex, debug);
                    break;

                case '1':
                    System.out.println("\n\nPlease input the string you wish to evaluate:");
                    String text = input.nextLine();
                    String result = EvaluateDFA.evaluate(DFA, text);
                    System.out.println(result);
                    break;

                case '2':
                    System.out.println("\n\nPlease input your filename:");
                     text = readFile(input.nextLine());
                     result = EvaluateDFA.evaluate(DFA, text);
                    System.out.println(result);
                    break;

                case '3':
                    debug = !debug;
                    System.out.println("\n\nDebug mode is now " + debug);
                    break;

                case '4':
                    stop = true;
                    break;

                default:
                    System.out.println("\n\nInvalid input, please enter an integer from 0 to 4.");
            }
        }

        input.close();
    }
}
