import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import au.com.bytecode.opencsv.CSVReader;
public class Preprocessrestaurant {
	public static HashMap<String, String> restidrating;
    public static void run() 
    {
    	restidrating = new HashMap<String, String>();
        try {
            System.out.println("\n**** readLineByLineExample ****");
            String csvFilename = "/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/restaurant.csv";
            CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
            String[] col = null;
            int i=0;
            while ((col = csvReader.readNext()) != null) 
            {
            	if(col[3].isEmpty()||i==0)
            		{
            		i++;
            		continue;
            		}
            	else
            	{
            		restidrating.put(col[0],col[3]);
            		System.out.println(col[3]);
            		i++;
            	}
            }
            csvReader.close();
        }
        catch(ArrayIndexOutOfBoundsException ae)
        {
            System.out.println(ae+" : error here");
        }catch (FileNotFoundException e) 
        {
            System.out.println("asd");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("");
            e.printStackTrace();
        }
    }
}