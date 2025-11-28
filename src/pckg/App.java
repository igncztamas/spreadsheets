package pckg;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class App extends JFrame
{
    static private Table table;

    public App()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            
        table = new Table(this, new DefaultTableModel());
  
        add(table.getScrollPane());

        createControls(table, this);

        setSize(1280, 720);
    }

    static private void createControls(Table t, JFrame f)
    {
        JButton button = new JButton("File");
        JPopupMenu menu = new FilePopUp(t, f);
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                menu.show(button, button.getX(), button.getY() + button.getHeight());
            }
            
        });
        JPanel panel0 = new JPanel(new BorderLayout(8,0));
        JPanel panel1 = new JPanel(new BorderLayout(8,0));
        JPanel panel2 = new JPanel(new BorderLayout(8,0));
        
        f.add(panel0, BorderLayout.NORTH);
        panel0.add(panel1, BorderLayout.WEST);
        panel0.add(panel2, BorderLayout.CENTER);
        
        JLabel valueLabel = new JLabel("Selected value: ");
        panel1.add(button, BorderLayout.WEST);
        panel1.add(valueLabel, BorderLayout.CENTER);

        t.getSelectedValue().setEditable(false);
        panel2.add(t.getSelectedValue(), BorderLayout.CENTER);
    }
    
    public static void main(String[] args) throws Exception
    {
        JFrame f = new App();
        
        f.setVisible(true);
    }
}
