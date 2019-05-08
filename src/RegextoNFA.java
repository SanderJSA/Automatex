@SuppressWarnings("Duplicates")

public class RegextoNFA {
    private int index;
    private String regex;
    private Graph NDFA;


    public RegextoNFA(String regex)
    {
        index = 1;
        this.regex = preProcess(regex);
        NDFA = new Graph("NDFA");
    }

    public static Graph regexToNDFA(String regex, boolean debug)
    {
        //Instantiate and generate AST
        RegextoNFA parser = new RegextoNFA(regex);
        Expression parsed =  parser.regex();

        if (debug)
        {
            System.out.println("Preprocessed regex: " + parser.regex);
            System.out.println("Generated AST: " + parsed);
        }

        //Generate NFA from AST
        parser.expressionToNDFA(parsed);
        parser.NDFA.getFinish().setAcceptState(true);
        return parser.NDFA;
    }

    //region preProcess
    private String preProcess(String input)
    {
        StringBuilder regexBuilder = new StringBuilder(input);
        for (int i = 0; i < regexBuilder.length(); i++)
        {
            if (regexBuilder.charAt(i) == '[')
            {
                regexBuilder.setCharAt(i,'(');
                i += 2;
                while (i < regexBuilder.length() && regexBuilder.charAt(i) != ']')
                {
                    if (regexBuilder.charAt(i) != '-')
                    {
                        regexBuilder.insert(i++, '|');
                    }
                    else
                    {
                        //remove '-'
                        regexBuilder.deleteCharAt(i);

                        //Insert every characters separated by '|' between start and finish
                        char start = regexBuilder.charAt(i-1);
                        start++;
                        char end = regexBuilder.charAt(i);

                        while (start < end)
                        {
                            regexBuilder.insert(i++, '|');
                            regexBuilder.insert(i++, start);
                            start++;
                        }
                        regexBuilder.insert(i++, '|');
                    }
                    i++;
                }

                //Check for invalid regex
                if (i != regexBuilder.length())
                {
                    regexBuilder.setCharAt(i, ')');
                }
                else
                {
                    System.out.println("Invalid regex, unmatched [");
                    return "()";
                }

            }
        }
        return "(" + regexBuilder + ")";
    }

    private boolean isAlphabet(char character)
    {
        return character != '(' && character != ')' && character != '|' && !isUnary(character);
    }

    private boolean isUnary(char character)
    {
        return character == '*' || character == '+' || character == '?';
    }
    //endregion

    //region Recursive descent
    private char peek()
    {
        return regex.charAt(index);
    }

    private char pop()
    {
        return regex.charAt(index++);
    }

    /*
    The algorithm is based on the following EBNF grammar

   <regex>  ::= <term> '|' <regex>
            |   <term>

   <term>   ::= { <factor> }

   <factor> ::= <base> { '*' }{ '+' }{ '?' }

   <base>   ::= <char>
            |   '\' <char>
            |   '(' <regex> ')'
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
        //if statement to remove redundant empty expression
        Expression factor = new Expression.Symb(null);
        if (peek() != ')' && peek() != '|')
        {
            factor = factor();
        }

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
                    base = new Expression.Or(base, new Expression.Symb(null));
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

        char symb = pop();

        //Check escaped characters
        if (symb == '\\')
        {
            symb = (peek() == 'n') ? '\n' : peek();
            pop();
        }

        //Check for wildcard
        if (symb == '.')
        {
            symb = '\u200B';
        }

        NDFA.addSymbol(symb);
        return new Expression.Symb(symb);
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
        if (expr.getSymb() == null)
            initial.addEpsilon(finish);
        else
            initial.addTransition(expr.getSymb(), finish);

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
        aF.addEpsilon(bI);

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
        initial.addEpsilon(aI);
        initial.addEpsilon(bI);
        aF.addEpsilon(finish);
        bF.addEpsilon(finish);

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
        initial.addEpsilon(finish);
        initial.addEpsilon(aI);
        aF.addEpsilon(aI);
        aF.addEpsilon(finish);

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
