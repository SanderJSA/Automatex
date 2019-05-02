public abstract class Expression
{

    public static class Or extends Expression
    {
        private Expression left;
        private Expression right;

        public Or(Expression left, Expression right)
        {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "or(" + left.toString() + ", " + right.toString() + ")";
        }

        public Expression getLeft() {
            return left;
        }

        public Expression getRight() {
            return right;
        }
    }

    public static class And extends Expression
    {
        private Expression left;
        private Expression right;

        public And(Expression left, Expression right)
        {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "and(" + left.toString() + ", " + right.toString() + ")";
        }

        public Expression getLeft() {
            return left;
        }

        public Expression getRight() {
            return right;
        }
    }

    public static class Star extends Expression
    {
        private Expression expr;

        public Star(Expression expr)
        {
            this.expr = expr;
        }

        @Override
        public String toString() {
            return "star(" + expr.toString() + ")";
        }

        public Expression getExpr() {
            return expr;
        }
    }

    public static class Symb extends Expression
    {
        private Character symb;

        public Symb(Character symb)
        {
            this.symb = symb;
        }

        @Override
        public String toString() {
            return "'"+ symb + "'";
        }

        public Character getSymb() {
            return symb;
        }
    }

    public static class Empty extends Expression
    {
        
    }
}

