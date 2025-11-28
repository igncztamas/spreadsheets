package pckg;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

class CellRenderer extends JTextArea implements TableCellRenderer
{
    public CellRenderer()
    {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        setText(value == null ? "" : value.toString());

        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }
        else
        {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        return this;
    }
}