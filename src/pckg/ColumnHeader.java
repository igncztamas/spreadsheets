package pckg;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;

public class ColumnHeader extends JTableHeader
{
    public ColumnHeader(Table table)
    {
        super(table.getColumnModel());
        setReorderingAllowed(false);
        
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (resizingColumn == null)
                {
                    int clickedIndex = table.columnAtPoint(e.getPoint());

                    if (e.isShiftDown())
                    {
                        table.addColumnSelectionInterval(clickedIndex, clickedIndex);
                        table.addRowSelectionInterval(0, table.getRowCount() - 1); 
                    }
                    else
                    {
                        table.setColumnSelectionInterval(clickedIndex, clickedIndex); 
                        table.setRowSelectionInterval(0, table.getRowCount() - 1); 
                    }
                }
            }
        });
    }        
}
