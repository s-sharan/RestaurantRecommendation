import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import com.opencsv.CSVWriter;
import au.com.bytecode.opencsv.CSVReader;

public class Dataformatterrestaurant 
{
	public static void run()  
    {
		String restid ="";
		String restname="";
		String cuisine="";
		String phno="";
		String add="";
		String hrs ="";
		String stars="";
        try {
            System.out.println("\n**** readrestaurant.csv ****");
            String csvFilename = "/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/restaurant.csv";
            CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
            String[] col = null;
            int i=0;
            while ((col = csvReader.readNext()) != null) 
            {
            	if(col[0].isEmpty()||i==0)
            		{
            		i++;
            		continue;
            		}
            	else
            	{
            		restid=col[0];
            		restname=col[1];
            		cuisine=col[2];
            		phno=col[3];
            		add=col[4];
            		hrs=col[5];
            		stars=col[6];
            		readrestsent(restid,restname,cuisine,phno,add,hrs,stars);
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
	public static void readrestsent(String restid,String restname,String cuisine,String phno,String add,String hrs,String stars)
	{
		String restsenti ="";
		String sentiment="";
		double numsentval=0;
		int count=0;
		double sum=0;
		try {
            System.out.println("\n**** readrestsentiment.csv ****");
            String csvFilename = "/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/restsentiment.csv";
            CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
            String[] col = null;
            int i=0;
            while ((col = csvReader.readNext()) != null) 
            {
            	if(col[0].isEmpty())
            		{
            		i++;
            		continue;
            		}
            	else
            	{
            		restsenti=col[0];
            		sentiment=col[1];
            		if(sentiment.equals("NaN"))
            		{
            			i++;
            			continue;
            		}
            		i++;
            		String[] splitstring = restsenti.split("\\|");
            		
            		if(splitstring[0].equals(restid))
            		{
            			count++;
            			numsentval = Double.parseDouble(sentiment);
            			sum=sum+numsentval;
            		}
            	}
            }
            csvReader.close();
            writeintocsv(restid,restname,cuisine,phno,add,hrs,stars,count,sum);
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
	public static void writeintocsv(String restid,String restname,String cuisine,String phno,String add,String hrs,String stars,int count,double sum)
	{
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter("/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/newrestaurant.csv",true));
		     String[] entries = new String[9];
		     entries[0]=restid;
		     entries[1]=restname;
		     entries[2]=cuisine;
		     entries[3]=phno;
		     entries[4]=add;
		     entries[5]=hrs;
		     entries[6]=stars;
		     entries[7]=count+"";
		     entries[8]=sum+"";
		     System.out.println("count"+entries[7]);
		     System.out.println("sent"+entries[8]);
		     writer.writeNext(entries);
			 writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}