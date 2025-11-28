package pckg;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class Table extends JTable
{    
    private int cid;
    private int rid;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JFrame frame;

    private int[] copiedColumns;
    private int[] copiedRows;
    private Map<Point, Object> clipboard;

    private Map<Point, ArrayList<CellFormula>> dependentFormulas;
    private Map<Point, CellFormula> formulas;
    private LinkedList<Point> hasCalledEval;

    private RowHeader rowHeader;
    private ColumnHeader columnHeader;
    private JTextField selectedValue;

    public Table(JFrame f, DefaultTableModel m)
    {
        super(m);
        reset();
        tableModel = m;
        frame = f;        

        selectedValue = new JTextField("");
        
        setAutoResizeMode( 0);
        setCellSelectionEnabled(true);
        setRowHeight(20);
        
        columnHeader = new ColumnHeader(this);
        setTableHeader(columnHeader);
        
        rowHeader = new RowHeader(this);      
        scrollPane = new JScrollPane(this);
        scrollPane.setRowHeaderView(rowHeader);
                
        addListeners();
        
        mapInputs();
        
        setDefaultEditor(Object.class, new CellEditor());
        setDefaultRenderer(Object.class, new CellRenderer());
    }
    
    public JScrollPane getScrollPane()
    {
        return scrollPane;
    }

    public JTextField getSelectedValue()
    {
        return selectedValue;
    }

    public void reset()
    {
        cid = 0;
        rid = 0;
        clipboard = new HashMap<Point, Object>();
        dependentFormulas = new HashMap<>();
        formulas = new HashMap<>();
        hasCalledEval = new LinkedList<Point>();
    }

    @Override
    public void setValueAt(Object aValue, int row, int column)
    {
        if (row == -1 || column == -1)
            return;

        while (column > getColumnCount()-1)
        {
            tableModel.addColumn(cid++);
        }
        
        while (row > getRowCount()-1)
        {
            tableModel.addRow(new Object[getColumnCount()]);
            ((DefaultListModel<String>)rowHeader.getModel()).addElement(rid++ + " ");
        }

        super.setValueAt(aValue, row, column);
    }  
  
    public void fill()
    {
        while (getColumnModel().getTotalColumnWidth() - scrollPane.getViewport().getViewPosition().x < frame.getWidth())
        {
            tableModel.addColumn(cid++);
        }
        
        while (getRowCount() - scrollPane.getViewport().getViewPosition().y/16 < frame.getHeight()/16)
        {
            tableModel.addRow(new Object[getColumnCount()]);
            ((DefaultListModel<String>)rowHeader.getModel()).addElement(rid++ + " ");
        }
    }

    private void addListeners()
    {
        scrollPane.addMouseWheelListener(new MouseAdapter()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                fill();
            }
        });
        frame.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                fill();
            }
        });

        ListSelectionListener selectionListener = new ListSelectionListener()
        {
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if (getSelectedRow() == -1 || getSelectedColumn() == -1)
                    return;
                Object o = getValueAt(getSelectedRow(), getSelectedColumn());
                if (o != null)
                    selectedValue.setText(o.toString());
                else
                    selectedValue.setText("");
            }
            
        };
        getSelectionModel().addListSelectionListener(selectionListener);
        getColumnModel().getSelectionModel().addListSelectionListener(selectionListener);

        tableModel.addTableModelListener(new TableModelListener()
        {
            boolean ignoreUpdate = false;
            @Override
            public void tableChanged(TableModelEvent e)
            {
                if (ignoreUpdate)
                    return;
                Point p = new Point(e.getColumn(),e.getFirstRow());
                if (formulas.containsKey(p))
                {
                    formulas.get(p).removeDependencies(dependentFormulas);
                    formulas.remove(p);
                }
                if (e.getType() == TableModelEvent.UPDATE && p.y != -1 && p.x != -1 && getValueAt(p.y, p.x) != null)
                {
                        String s = getValueAt(p.y, p.x).toString();
                        if (s.startsWith("{") && s.endsWith("}"))
                        {
                            ignoreUpdate = true;
                                setValueAt(new CellFormula(s, dependentFormulas, p, Table.this), p.y, p.x);
                            ignoreUpdate = false;

                            formulas.put(p, (CellFormula)getValueAt(p.y, p.x));                            
                        }
                }
                updateCellsRecursive(p, Table.this);
                hasCalledEval.clear();
            }
        });
    }

    private void updateCellsRecursive(Point p, Table t)
    {
        if (dependentFormulas.get(p) != null)
        {
            if (hasCalledEval.contains(p))
                ((CellFormula)getValueAt(p.y, p.x)).fail();
            else
                hasCalledEval.add(p);
            for (CellFormula i : dependentFormulas.get(p))
            {
                if (i.evaluate())
                    updateCellsRecursive(i.getPoint(), t);
            }
            repaint();
        }
    }

    private void mapInputs()
    {
        getInputMap().put(KeyStroke.getKeyStroke("control C"), "copy");
        getActionMap().put("copy", new AbstractAction()
       {
                @Override
                public void actionPerformed(ActionEvent e)
                {    
                    copiedColumns = getSelectedColumns(); 
                    copiedRows = getSelectedRows();
                    for (int i : getSelectedColumns())
                    {
                        for (int ii : getSelectedRows())
                            clipboard.put(new Point(ii, i), tableModel.getValueAt(ii, i));
                    }
                }
        });
        rowHeader.getInputMap().put(KeyStroke.getKeyStroke("control C"), "copy");
        rowHeader.getActionMap().put("copy", getActionMap().get("copy"));

        getInputMap().put(KeyStroke.getKeyStroke("control V"), "paste");
        getActionMap().put("paste", new AbstractAction()
        {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (clipboard.isEmpty())
                        return;
                    int xMin = Arrays.stream(copiedRows).min().getAsInt();
                    int yMin = Arrays.stream(copiedColumns).min().getAsInt();
                    for (int i : copiedColumns)
                    {
                        for (int ii : copiedRows)
                            setValueAt(clipboard.get(new Point(ii,i)), getSelectedRow() + ii - xMin, getSelectedColumn() + i - yMin);
                    }
                }
        });
        rowHeader.getInputMap().put(KeyStroke.getKeyStroke("control V"), "paste");
        rowHeader.getActionMap().put("paste", getActionMap().get("paste"));

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        getActionMap().put("delete", new AbstractAction()
        {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    for (int i : getSelectedColumns())
                    {
                        for (int ii : getSelectedRows())
                            setValueAt("", ii, i);
                    }
                }
        });
        rowHeader.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        rowHeader.getActionMap().put("delete", getActionMap().get("delete"));

    }
}
