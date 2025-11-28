package pckg;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import hirondelle.formula.Decimal;
import hirondelle.formula.UnknownFunctionException;
import hirondelle.formula.function.Function;

public class FormulaFunctions
{
    private Table table;
    private Map<String, Function> map = new HashMap<String, Function>();

    public FormulaFunctions(Table t)
    {
        table = t;

        map.put("SUM", new Sum());
        map.put("COUNT", new Count());
        map.put("AVG", new Avg());
        map.put("MIN", new Min());
        map.put("MAX", new Max());
    }

    public Map<String, Function> getFunctions()
    {
        return map;
    }

    class CustomFunction implements Function
    {
        protected boolean eval;
        protected Decimal r;
        private LinkedList<Point> dependents;

        public CustomFunction()
        {
            dependents = new LinkedList<Point>();
            r = Decimal.ZERO;
            eval = false;
        }

        public LinkedList<Point> getDependents()
        {
            return dependents;
        }

        @Override
        public Decimal calculate(Decimal... aArgs)
        {
            r = Decimal.ZERO;
            eval = false;
            if (aArgs.length > 4)
                throw new UnknownFunctionException();
            for (int i = aArgs[1].intValue(); i <= aArgs[3].intValue(); i++)
            {
                for (int ii = aArgs[0].intValue(); ii <= aArgs[2].intValue(); ii++)
                {
                    dependents.add(new Point(ii, i));
                    
                    Object o = table.getValueAt(i, ii);
                    if (o != null && !o.toString().isEmpty())
                        rIncrement(Decimal.from(o.toString()));
                }
            }
            if (!eval)
                return null;
            rFinal();
            return r;
        }
        protected void rIncrement(Decimal d) {}
        protected void rFinal() {}
    }

    class Sum extends CustomFunction
    {
        @Override
        protected void rIncrement(Decimal d)
        {
            r = r.plus(d);
            eval = true;
        }
    }
    class Count extends CustomFunction
    {
        @Override
        protected void rIncrement(Decimal d)
        {
            r = r.plus(1);
            eval = true;
        }
    }
    class Avg extends CustomFunction
    {
        Decimal c = Decimal.ZERO;

        @Override
        protected void rIncrement(Decimal d)
        {
            r = r.plus(d);
            c = c.plus(1);
            eval = true;
        }
        @Override
        protected void rFinal()
        {
            r = r.div(c);
        }
    }
    class Min extends CustomFunction
    {
        @Override
        protected void rIncrement(Decimal d)
        {
            if (r.compareTo(d) > 0 || !eval)
            {
                r = d;
                eval = true;
            }
        }
    }
    class Max extends CustomFunction
    {
        @Override
        protected void rIncrement(Decimal d)
        {
            if (r.compareTo(d) < 0 || !eval)
            {
                r = d;
                eval = true;
            }
        }
    }
}
