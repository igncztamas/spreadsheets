package pckg;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.UIManager;

public class RowHeader extends JList<String>
{
    public RowHeader(Table table)
    {
        super(new DefaultListModel<String>()); 

        setFixedCellWidth(50);

        MouseAdapter mouseAdapter = new MouseAdapter()
        {
            private boolean sizing = false;
            private int i;
            private int prevY;
        
            @Override
            public void mousePressed(MouseEvent e)
            {
                sizing = getCursor().equals(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                prevY = e.getY();

                if (!sizing)
                {
                    i = table.rowAtPoint(e.getPoint());
                    if (e.isShiftDown())
                    {
                        table.addRowSelectionInterval(i, i);
                        table.addColumnSelectionInterval(0, table.getColumnCount()-1);
                    }
                    else
                    {
                        table.setRowSelectionInterval(i, i);
                        table.setColumnSelectionInterval(0, table.getColumnCount()-1);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if (sizing)
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    sizing = false;
                }
            }
                    
            @Override
            public void mouseMoved(MouseEvent e)
            {
                i = table.rowAtPoint(e.getPoint());
                int y1 = getCellBounds(i, i).y;
                int y2 = getCellBounds(i+1, i+1).y;
                
                if (Math.abs(y1 - e.getY()) < 4 && i != 0)
                {
                    setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                    i--;
                }
                else if (Math.abs(y2 - e.getY()) < 4)
                    setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                else 
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseDragged(MouseEvent e)
            {
                if (sizing && i >= 0)
                {
                    table.setRowHeight(i,  Math.max(table.getTableHeader().getHeight(), table.getRowHeight(i) + e.getY() - prevY));

                    prevY = e.getY();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        class RowHeaderRenderer extends DefaultListCellRenderer
        {
            public RowHeaderRenderer()
            {
                super();
                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setForeground(table.getTableHeader().getForeground());
                setBackground(table.getTableHeader().getBackground());
                setFont(table.getTableHeader().getFont());
                setHorizontalAlignment(CENTER);
            }

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                setText(value.toString());
                setPreferredSize(new Dimension(50, table.getRowHeight(index)));
                list.firePropertyChange("cellRenderer", 0, 1);
                return this;
            }
        }

        setCellRenderer(new RowHeaderRenderer());
    }   
}
