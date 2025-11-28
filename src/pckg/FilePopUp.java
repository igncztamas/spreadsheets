package pckg;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

class FilePopUp extends JPopupMenu
{
    public FilePopUp(Table table, JFrame frame)
    {
        JPanel savePanel = new JPanel(new BorderLayout(8,0));
        JPanel loadPanel = new JPanel(new BorderLayout(8,0));
        JPanel readPanel = new JPanel(new BorderLayout(8,0));
        Dimension bd = new Dimension(64,20);
        JTextField saveField = new JTextField(16);
            saveField.setToolTipText("file");
        JTextField loadField = new JTextField(16);
            loadField.setToolTipText("file");
        JTextField readField1 = new JTextField(4);
            readField1.setToolTipText("delimiter");
        JTextField readField2 = new JTextField(16);
            readField2.setToolTipText("file");
        
        JButton save = new JButton("save");
            save.setPreferredSize(bd);
            save.addActionListener(new ActionListener()
            {     
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        TableIO.save(table, saveField.getText());
                        frame.setTitle(saveField.getText());
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });

        JButton load = new JButton("load");
            load.setPreferredSize(bd);
            load.addActionListener(new ActionListener()
            {     
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        TableIO.load(table, loadField.getText());
                        frame.setTitle(loadField.getText());
                    }
                    catch (ClassNotFoundException | IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });

        JButton read = new JButton("read");
            read.setPreferredSize(bd);
            read.addActionListener(new ActionListener()
            {     
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {
                        String s = readField1.getText();
                        TableIO.read(table, readField2.getText(), s.isEmpty() ? ',' : s.charAt(0));
                        frame.setTitle(readField2.getText());
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });

        savePanel.add(save, BorderLayout.WEST);
        savePanel.add(saveField, BorderLayout.CENTER);
        loadPanel.add(load, BorderLayout.WEST);
        loadPanel.add(loadField, BorderLayout.CENTER);
        readPanel.add(read, BorderLayout.WEST);
        readPanel.add(readField1, BorderLayout.CENTER);
        readPanel.add(readField2, BorderLayout.EAST);
        add(savePanel);
        add(loadPanel);
        add(readPanel);
    }
}
