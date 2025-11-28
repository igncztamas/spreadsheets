package pckg;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class TableIO
{
    public static void load(Table t, String s) throws IOException, ClassNotFoundException
    {
        try
        {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(s));
            TableData data = (TableData)is.readObject(); 
            data.readData(t);
            is.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("No such file.");
        }
    }
    
    public static void save(Table t, String s) throws IOException, FileNotFoundException
    {
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(s));
        os.writeObject(new TableData(t));
        os.close();
    }

    public static void read(Table t, String file, char delimiter) throws IOException
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = br.readLine();
            ArrayList<String[]> arr = new ArrayList<>();
            while (s != null)
            {
                boolean inFormula = false;
                String newCell = new String();
                ArrayList<String> newLine = new ArrayList<String>();
                for (char i : s.toCharArray())
                {
                    if (i == delimiter && !inFormula)
                    {
                        newLine.add(newCell);
                        newCell = new String();
                    }
                    else
                    {
                        inFormula = (inFormula || i == '{') && !(i == '}');
                        newCell = newCell.concat(String.valueOf(i));
                    }
                }
                newLine.add(newCell);
                arr.add(newLine.toArray(new String[newLine.size()]));
                s = br.readLine();
            }
            br.close();
    
            TableData data = new TableData(arr);
            data.readData(t);
        }
        catch(FileNotFoundException e)
        {
            System.out.println("No such file.");
        }
    }
}
