import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public class mainmainscipt 
{
	public static void main(String args[])
	{
		mainscript ms=new mainscript();
		ms.run();
		Dataformattermenu dmenu= new Dataformattermenu();
		Dataformatterrestaurant drest = new Dataformatterrestaurant();
		dmenu.run();
		drest.run();
	}
}
