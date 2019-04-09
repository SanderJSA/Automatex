import java.util.Scanner;

public class Automatex {

    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("Please input your regular expression : ");
            String regex = input.nextLine();

            Graph NDFA = ThompsonConstruction.regexToNDFA(regex);
            NDFA.saveGraph();
        }
    }
}
