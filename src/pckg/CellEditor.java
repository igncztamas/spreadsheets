package pckg;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

public class CellEditor extends DefaultCellEditor
{
    public CellEditor()
    {
        super(new JTextField());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        delegate.setValue(value);
        if (value != null && value.getClass() == CellFormula.class)
        {
            ((JTextField)editorComponent).setText(((CellFormula)value).getFormula());
            return editorComponent;
        }
        else
            return editorComponent;
    }
}
