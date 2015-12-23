import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import com.opencsv.CSVWriter;

import au.com.bytecode.opencsv.CSVReader;
public class mainscript 
{
	public static void run() 
	{
		// TODO Auto-generated method stub
		Preprocessmenu prepromenu = new Preprocessmenu();
		prepromenu.run();
		Preprocessrestaurant preprorest = new Preprocessrestaurant();
		preprorest.run();
		Preprocessreview preproreview = new Preprocessreview();
		preproreview.run();
		HashMap<String,String> restmenusenti = new HashMap<String,String>();
		HashMap<String,String> restoverallsenti = new HashMap<String,String>();
		MYSentiment senti = new MYSentiment();
		ArrayList<ArrayList<String>> sentimentvalfinal= new ArrayList<ArrayList<String>>();
		ArrayList<String> sentimentval = new ArrayList<String>();
		for (HashMap.Entry<String, String> entry : preproreview.restiddatereview.entrySet()) 
		{
		    System.out.println("Key = " + entry.getKey().charAt(0) + ", Value = " + entry.getValue());
			try {
				senti.run(entry.getValue(),entry.getKey(),prepromenu.restidmenuitem,preproreview.restiddatereview,restmenusenti,restoverallsenti);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (HashMap.Entry<String, String> entry : restmenusenti.entrySet()) 
		{
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			 CSVWriter writer;
			try {
				writer = new CSVWriter(new FileWriter("/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/dishsentiment3.csv",true));
			     String[] entries = new String[2];
			     entries[0]=entry.getKey();
			     entries[1]=entry.getValue();
			     writer.writeNext(entries);
				 writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		for (HashMap.Entry<String, String> entry : restoverallsenti.entrySet()) 
		{
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			 CSVWriter writer;
			try {
				writer = new CSVWriter(new FileWriter("/Users/vikasmohandoss/Documents/Cloud/CloudProject/NewData/restsentiment.csv",true));
			     String[] entries = new String[2];
			     entries[0]=entry.getKey();
			     entries[1]=entry.getValue();
			     writer.writeNext(entries);
				 writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
//		try {
//			sentimentval=senti.run("What a superb little restaurant! I had the Asian Salmon as an entree -- it was phenomenal! And the host is very friendly/ the service is great. A must if you're in the area!","2",prepromenu.restidmenuitem,preproreview.restiddatereview);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(sentimentval);
	}
}
