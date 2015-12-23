import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.hcoref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.*;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.Properties;

public class MYSentiment 
{
    public static void run(String textval,String keyval,HashMap<String, String> restidmenuitem,HashMap<String, String> restiddatereview,HashMap<String,String> restmenusenti,HashMap<String,String> restoverallsenti) throws Exception
    {
    	ArrayList<String> sentivals = new ArrayList<String>();
    	ArrayList<String> stopwords = new ArrayList<String>();
    	ArrayList<String> menuitemsdesc = new ArrayList<String>();
    	ArrayList<String> menuitemsval = new ArrayList<String>();
    	ArrayList<String> keytobestored = new ArrayList<String>();
    	String numsentival,avgsentival;
    	Scanner s = new Scanner(new File("/Users/vikasmohandoss/Documents/Cloud/CloudProject/stopwords_en.txt"));
    	while (s.hasNext()){
    	    stopwords.add(s.next());
    	}
    	s.close();
	    String c1="";
	    String[] splitstring = keyval.split("\\|");
	    c1=splitstring[0];
	    System.out.println("c1val="+c1);
    	for (HashMap.Entry<String, String> entry : restidmenuitem.entrySet()) 
		{
		    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    String c2 = "";
		    String[] splitstring2 = entry.getKey().split("\\|");
		    c2=splitstring2[0];
		    System.out.println("c2val="+c2);
		    if(c1.equals(c2))
		    {
		    	menuitemsdesc.add(entry.getValue());
		    	menuitemsval.add(splitstring2[1]);
		    	System.out.println(entry.getValue());
		    	System.out.println(splitstring2[1]);
		    }
		}
        Annotation document = new Annotation(textval);
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        pipeline.annotate(document);
        int k=0,k2=0,k3=0,k4=0,k5=0,flag=0;
        String sentval;
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) 
        {
        	flag=0;
        	k=0;
        	k2=0;        	
            System.out.println("---");
            System.out.println(sentence);
            sentval=sentence.toString().toLowerCase();
            System.out.println(sentval);
            String str = sentval;
            String[] splited = str.split("\\b+"); //split on word boundries
            for(k=0;k<menuitemsdesc.size();k++)
            {
            k2=0;
            Boolean iscontain=false;
            String[] splited2 = menuitemsdesc.get(k).split("\\s*(,|\\s)\\s*");
            String[] splited3 = menuitemsval.get(k).split("\\s*(,|\\s)\\s*");
//            for(k2=0;k2<splited2.length;k2++)
//            {
//            	for(k4=0;k4<splited.length;k4++)
//            	{
//            		flag=0;
//            		for(k5=0;k5<stopwords.size();k5++)
//            		{
//            			if(stopwords.get(k5).equals(splited[k4]))
//            			{
//            				flag=1;
//            				break;
//            			}
//            		}
//            		if(flag==1)
//            			continue;
//            	iscontain=splited[k4].equals(splited2[k2].toLowerCase());
//            	if(iscontain==true)
//            	{
//            		System.out.println("check1"+splited2[k2].toLowerCase());
//            	break;
//            	}
//            	}
//            	if(iscontain==true)
//                	break;
//            }
            if(iscontain==false)
            {
            for(k2=0;k2<splited3.length;k2++)
            {
            	//System.out.println("checksplited2"+splited3[k2].toLowerCase());
            //iscontain=Arrays.asList(splited).equals(splited3[k2].toLowerCase());//search array for word
            	for(k4=0;k4<splited.length;k4++)
            	{
            		flag=0;
            		for(k5=0;k5<stopwords.size();k5++)
            		{
            			if(stopwords.get(k5).equals(splited[k4]))
            			{
            				flag=1;
            				break;
            			}
            		}
            		if(flag==1)
            			continue;
            	iscontain=splited[k4].equals(splited3[k2].toLowerCase());
            	if(iscontain==true)
            	{
            		System.out.println("check2"+splited3[k2].toLowerCase());
            	break;
            	}
            	}
            if(iscontain==true)
            	break;
            }
            }
            if(iscontain==true)
            {
            System.out.println(sentence.get(SentimentAnnotatedTree.class));
            System.out.println(sentence.get(SentimentClass.class));
            numsentival=getnumval(sentence.get(SentimentClass.class));
            System.out.println(menuitemsval.get(k));
            restmenusenti.put(c1+"|"+menuitemsval.get(k)+"|"+sentence.toString(),numsentival);
            System.out.println(c1+"|"+menuitemsval.get(k)+"|"+sentence.toString()+numsentival);
            }
            }
            numsentival=getnumval(sentence.get(SentimentClass.class));
            sentivals.add(numsentival);
        }
        System.out.println(sentivals);
        avgsentival=getavg(sentivals);
        System.out.println(avgsentival);
        restoverallsenti.put(c1+"|"+textval,avgsentival);
        //return avgsentival;
    }
    public static String getavg(ArrayList<String> sentivals)
    {
    	int t1;
    	double sum=0,avg=0;
    	int xfactor=0;
    	String temp,result;
    	for(t1=0;t1<sentivals.size();t1++)
    	{
    		temp=sentivals.get(t1);
    		if(temp.equals("5"))
    			sum=sum+5;
    		else if(temp.equals("4"))
    			sum=sum+4;
    		else if(temp.equals("3"))
    		{
    			sum=sum+0;
    			xfactor=xfactor+1;
    		}
    		else if(temp.equals("2"))
    			sum=sum+2;
    		else if(temp.equals("1"))
    			sum=sum+1;
    		else
    			sum=sum+0;
    	} 
    	System.out.println(sum);
    	System.out.println(sentivals.size()-xfactor);
    	avg=sum/(sentivals.size()-xfactor);
    	System.out.println(avg);
    	return avg+"";
    }
    public static String getnumval(String sentivalue)
    {
    	String tempval = null;
    	if(sentivalue.equals("Very positive"))
    		tempval="5";
    	else if(sentivalue.equals("Positive"))
    		tempval="4";
    	else if(sentivalue.equals("Neutral"))
    		tempval="3";
    	else if(sentivalue.equals("Very negative"))
    		tempval="1";
    	else if(sentivalue.equals("Negative"))
    		tempval="2";
    	else
    		tempval="0";
		return tempval;
    }
}