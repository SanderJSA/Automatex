@SuppressWarnings("Duplicates")

public class ThompsonConstruction {
    private int index;
    private String regex;
    private Graph NDFA;


    public ThompsonConstruction(String regex)
    {
        index = 1;
        this.regex = preProcess(regex);
        System.out.println(this.regex);
        NDFA = new Graph("NDFA");
    }

    public static Graph regexToNDFA(String regex)
    {
        ThompsonConstruction parser = new ThompsonConstruction(regex);
        Expression parsed =  parser.parse();
        System.out.println(parsed);
        parser.expressionToNDFA(parsed);
        return parser.NDFA;
    }

    //region preProcess
    private String preProcess(String input)
    {
        for (int i = 1; i < input.length(); i++)
        {
            if (leftValid(input.charAt(i-1)) && rightValid(input.charAt(i)))
            {
                input = input.substring(0, i) + '.' + input.substring(i);
                i++;
            }
        }
        return "(" + input + ")";
    }

    private boolean leftValid(char left)
    {
        return left == ')' || left == '*' || isAlphabet(left);
    }

    private boolean rightValid(char right)
    {
        return right == '(' || isAlphabet(right);
    }

    private boolean isAlphabet(char character)
    {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z') || (character >= '0' && character <= '9');
    }
    //endregion

    //region Parser
    private char peek()
    {
        return regex.charAt(index);
    }

    private char pop()
    {
        return regex.charAt(index++);
    }

    private Expression parse()
    {
        Expression expr = new Expression.Empty();
        while(peek() != ')')
        {
            char sym = pop();
            switch (sym)
            {
                //Or
                case '|':
                    return new Expression.Or(expr, parse());
                    //break;
                //And
                case '.':
                    char next = pop();
                    Expression nextExpr = (next == '(') ? parse() : new Expression.Symb(next);
                    if (peek() == '*')
                    {
                        pop();
                        nextExpr = new Expression.Star(nextExpr);
                    }

                    expr = new Expression.And(expr, nextExpr);
                    break;
                //Star
                case '*':
                    expr = new Expression.Star(expr);
                    break;
                //(
                case '(':
                    expr = parse();
                    break;
                //Sym
                default:
                    expr = createSym(sym);
                    break;
            }
        }
        pop();
        return expr;
    }

    private Expression createSym(char sym)
    {
        Expression expr = new Expression.Symb(sym);

        if (peek() == '*')
        {
            pop();
            expr = new Expression.Star(expr);
        }

        return expr;
    }
    //endregion

    //region Expressions

    private void expressionToNDFA(Expression expr)
    {
        if (expr instanceof Expression.Symb)
            symbolExp((Expression.Symb)expr);
        else if (expr instanceof Expression.And)
            concExp((Expression.And) expr);
        else if (expr instanceof Expression.Or)
            unionExp((Expression.Or) expr);
        else
            kleenExp((Expression.Star)expr);
    }

    private void symbolExp(Expression.Symb expr)
    {
        //Get initial nodes
        Node initial = addNode();
        Node finish = addNode();

        //Perform Thompson's construction
        initial.addConnection(finish, String.valueOf(expr.getSymb()));

        //update states
        NDFA.setInitial(initial);
        NDFA.setFinish(finish);
    }

    private void concExp(Expression.And expr)
    {
        //Get initial nodes
        expressionToNDFA(expr.getLeft());
        Node aI = NDFA.getInitial();
        Node aF = NDFA.getFinish();
        expressionToNDFA(expr.getRight());
        Node bI = NDFA.getInitial();
        Node bF = NDFA.getFinish();

        //Perform Thompson's construction
        aF.addConnection(bI, null);

        //update states
        NDFA.setInitial(aI);
        NDFA.setFinish(bF);
    }

    private void unionExp(Expression.Or expr)
    {
        //Get initial nodes
        expressionToNDFA(expr.getLeft());
        Node aI = NDFA.getInitial();
        Node aF = NDFA.getFinish();
        expressionToNDFA(expr.getRight());
        Node bI = NDFA.getInitial();
        Node bF = NDFA.getFinish();
        Node initial = addNode();
        Node finish = addNode();

        //Perform Thompson's construction
        initial.addConnection(aI, null);
        initial.addConnection(bI, null);
        aF.addConnection(finish, null);
        bF.addConnection(finish, null);

        //update states
        NDFA.setInitial(initial);
        NDFA.setFinish(finish);
    }

    private void kleenExp(Expression.Star expr)
    {
        //Get initial nodes
        expressionToNDFA(expr.getExpr());
        Node aI = NDFA.getInitial();
        Node aF = NDFA.getFinish();
        Node initial = addNode();
        Node finish = addNode();

        //Perform Thompson's construction
        initial.addConnection(finish, null);
        initial.addConnection(aI, null);
        aF.addConnection(aI, null);
        aF.addConnection(finish, null);

        NDFA.setInitial(initial);
        NDFA.setFinish(finish);
    }

    //endregion

    private Node addNode()
    {
        Node state = new Node(NDFA.getNodes().size());
        NDFA.addNode(state);
        return state;
    }
}
