package pckg;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import hirondelle.formula.Decimal;
import hirondelle.formula.Formula;
import hirondelle.formula.function.Function;

public class CellFormula
{
    private Formula formula;
    private String value;
    private Point point;
    private LinkedList<Point> dependents;
    private FormulaFunctions f;
    private Table table;

    public CellFormula(String s, Map<Point, ArrayList<CellFormula>> map, Point pp, Table t)
    {
        point = pp;
        table = t;
        value = "unevaluated";
        f = new FormulaFunctions(table);
        formula = new Formula(s.substring(1, s.length()-1), new HashMap<String,Decimal>(), f.getFunctions());
        dependents = new LinkedList<>();

        evaluate();
        
        for (String i : formula.getVariableNames())
        {
            try
            {
                dependents.add(variableFormat(i));
            }
            catch(Exception e)
            {
                continue;
            }
        }
        for (Function i : formula.getCustomFunctions().values())
        {
            if (((FormulaFunctions.CustomFunction)i).getDependents() != null)
                dependents.addAll(((FormulaFunctions.CustomFunction)i).getDependents());
        }
        for (Point i : dependents)
        {
            if (!map.containsKey(i))
                map.put(i, new ArrayList<>());
            map.get(i).add(this);
        }
    }

    public Point getPoint()
    {
        return point;
    }

    public boolean evaluate() //check for recursion
    {
        String oldValue = value;
        Map<String, Decimal> variables = new LinkedHashMap<String, Decimal>();
        try
        {
            for (String i : formula.getVariableNames())
            {
                Point  p = variableFormat(i);
                Object o = table.getValueAt(p.y, p.x);
                variables.put(i, Decimal.from(o.toString()));
            }
            formula = new Formula(formula.getFormula(), variables, f.getFunctions());
            
            value = String.valueOf(formula.getAnswer());
            if (value == null || value.equals("null"))
                throw new Exception();
        }
        catch (Exception e)
        {
            value = "FormulaError";
        }
        return !value.equals(oldValue);
    }
    
    public String getFormula()
    {
        return "{" + formula.getFormula() + "}";
    }

    public void fail()
    {
        value = "FormulaError";
    }

    @Override
    public String toString()
    {
        return value;
    }

    public static Point variableFormat(String s) throws Exception
    {
        String[] split = s.split(":");
        if (split.length != 3 || !split[0].equals(""))
            throw new Exception();
        return new Point(Integer.valueOf(split[2]), Integer.valueOf(split[1]));
    }

    public void removeDependencies(Map<Point, ArrayList<CellFormula>> map)
    {
        for (Point i : dependents)
        {
            map.get(i).remove(this); 
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || o.getClass() != CellFormula.class)
            return false;
        else
            return o.toString().equals(value) && ((CellFormula)o).getFormula().equals(this.getFormula());
    }
}