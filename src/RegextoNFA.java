@SuppressWarnings("Duplicates")

public class RegextoNFA {
    private int index;
    private String regex;
    private Graph NDFA;


    public RegextoNFA(String regex)
    {
        index = 1;
        this.regex = preProcess(regex);
        System.out.println(this.regex);
        NDFA = new Graph("NDFA");
    }

    public static Graph regexToNDFA(String regex)
    {
        RegextoNFA parser = new RegextoNFA(regex);
        Expression parsed =  parser.regex();
        System.out.println(parsed);
        parser.expressionToNDFA(parsed);
        parser.NDFA.getFinish().setAcceptState(true);
        return parser.NDFA;
    }

    //region preProcess
    private String preProcess(String input)
    {
        for (int i = 1; i < input.length(); i++)
        {
            if (leftValid(input.charAt(i-1)) && rightValid(input.charAt(i)))
            {
                //input = input.substring(0, i) + '.' + input.substring(i);
                //i++;
            }
        }
        return "(" + input + ") ";
    }

    private boolean leftValid(char left)
    {
        return left == ')' || isUnary(left) || isAlphabet(left);
    }

    private boolean rightValid(char right)
    {
        return right == '(' || isAlphabet(right);
    }

    private boolean isAlphabet(char character)
    {
        //character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z') || (character >= '0' && character <= '9'
        return character != '(' && character != ')' && character != '|' && !isUnary(character);
    }

    private boolean isUnary(char character)
    {
        return character == '*' || character == '+' || character == '?';
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

    //Recursive descent
    /*
   <regex> ::= <term> '|' <regex>
            |  <term>

   <term> ::= { <factor> }

   <factor> ::= <base> { '*' }

   <base> ::= <char>
           |  '\' <char>
           |  '(' <regex> ')'
     */

    private Expression regex()
    {
        Expression term = term();
        if (peek() == '|')
        {
            pop();
            Expression regex = regex();
            return new Expression.Or(term, regex);
        }

        return term;
    }

    private Expression term()
    {
        Expression factor = new Expression.Symb('ε');

        while (peek() != ')' && peek() != '|')
        {
            Expression nextFactor = factor();
            factor = new Expression.And(factor, nextFactor);
        }

        return factor;
    }

    private Expression factor()
    {
        Expression base = base();

        while (isUnary(peek()))
        {
            switch (peek())
            {
                case '*':
                    pop();
                    base = new Expression.Star(base);
                    break;
                case '+':
                    pop();
                    base = new Expression.And(base, new Expression.Star(base));
                    break;
                case '?':
                    pop();
                    base = new Expression.Or(base, new Expression.Symb('ε'));
                    break;
                default:
                    System.out.println("Invalid unary operator : " + peek());
            }
        }

        return base;
    }

    private Expression base()
    {
        if (peek() == '(')
        {
            pop();
            Expression regex = regex();
            pop();
            return regex;
        }

        NDFA.addSymbol(peek());
        return new Expression.Symb(pop());
    }




    /*
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
                //And
                case '.':
                    char next = pop();
                    Expression nextExpr = (next == '(') ? parse() : createSym(next);

                    expr = new Expression.And(expr, nextExpr);
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
        return evalUnary(expr);
    }

    private Expression createSym(char sym)
    {
        Expression expr = new Expression.Symb(sym);
        NDFA.addSymbol(sym);
        return evalUnary(expr);
    }

    private Expression evalUnary(Expression expr)
    {
        switch (peek())
        {
            case '*':
                pop();
                System.out.println("huh");
                return new Expression.Star(expr);
            case '+':
                pop();
                return new Expression.And(expr, new Expression.Star(expr));
            case '?':
                pop();
                return new Expression.Or(expr, new Expression.Symb('ε'));
            default:
                return expr;
        }
    }
    */
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
        String symb = (expr.getSymb() == 'ε') ? null : String.valueOf(expr.getSymb());
        initial.addConnection(finish, symb);

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
