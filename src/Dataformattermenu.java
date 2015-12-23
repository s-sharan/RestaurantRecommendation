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
public class Dataformattermenu 
{
	public static void run()  
    {
		String rest ="";
		String item="";
		String price="";
		String course="";
		String descp="";
        try {
            System.out.println("\n**** readmenu.csv ****");
            String csvFilename = "/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/menus.csv";
            CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
            String[] col = null;
            int i=0;
            while ((col = csvReader.readNext()) != null) 
            {
            	if(col[4].isEmpty()||i==0)
            		{
            		i++;
            		continue;
            		}
            	else
            	{
            		rest=col[0];
            		item=col[1];
            		price=col[2];
            		course=col[3];
            		descp=col[4];
            		System.out.println(rest+"|"+item);
            		i++;
            		readdishsent(rest,item,price,course,descp);
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
	public static void readdishsent(String rest,String item,String price,String course,String descp)
	{
		String restdish ="";
		String sentiment="";
		int numsentval=0;
		int count=0;
		int sum=0;
		try {
            System.out.println("\n**** readdishsentiment.csv ****");
            String csvFilename = "/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/dishsentiment3.csv";
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
            		restdish=col[0];
            		sentiment=col[1];
            		i++;
            		String[] splitstring = restdish.split("\\|");
            		System.out.println(splitstring[0]);
            		System.out.println(splitstring[1]);
            		if(splitstring[0].equals(rest)&&splitstring[1].equals(item))
            		{
            			count++;
            			numsentval = Integer.parseInt(sentiment);
            			sum=sum+numsentval;
            		}
            	}
            }
            csvReader.close();
            writeintocsv(rest,item,price,course,descp,count,sum);
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
	public static void writeintocsv(String rest,String item,String price,String course,String descp,int count,int sum)
	{
		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter("/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/newmenu.csv",true));
		     String[] entries = new String[7];
		     entries[0]=rest;
		     entries[1]=item;
		     entries[2]=price;
		     entries[3]=course;
		     entries[4]=descp;
		     entries[5]=count+"";
		     entries[6]=sum+"";
		     writer.writeNext(entries);
			 writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}