package pckg;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.table.DefaultTableModel;

public class TableData implements Serializable
{
    private int rowCount;
    private int columnCount;
    private String[][] cellData;
    
    public TableData(Table t)
    {
        rowCount = t.getRowCount();
        columnCount = t.getColumnCount();

        cellData = new String[columnCount][rowCount];

        for (int i = 0; i < columnCount; i++)
        {
            for (int ii = 0; ii < rowCount; ii++)
            {
                Object o = t.getValueAt(ii, i);
                if (o == null)
                    cellData[i][ii] = null;
                else if (o.getClass() == CellFormula.class)
                    cellData[i][ii] = ((CellFormula)o).getFormula();
                else
                    cellData[i][ii] = o.toString();
            }
        }
    }

    public TableData(ArrayList<String[]> input)
    {  
        columnCount = Collections.max(input, Comparator.comparingInt(a -> a.length)).length;
        rowCount = input.size();

        cellData = new String[columnCount][rowCount];

        for (int ii = 0; ii < rowCount; ii++)
        {
            for (int i = 0; i < input.get(ii).length; i++)
            {
                String s = input.get(ii)[i];
                cellData[i][ii] = s.equals("") ? null : s;
            }
        }
    }

    public void readData(Table t)
    {
        t.reset();
        ((DefaultTableModel)t.getModel()).setColumnCount(0);
        ((DefaultTableModel)t.getModel()).setRowCount(0);
        for (int i = 0; i < columnCount; i++)
        {
            for (int ii = 0; ii < rowCount; ii++)
            {
                t.setValueAt(cellData[i][ii], ii, i);
            }
        }
        t.fill();
    }
}
