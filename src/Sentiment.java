import java.io.IOException;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.hcoref.CorefCoreAnnotations;
import edu.stanford.nlp.hcoref.data.CorefChain;
import edu.stanford.nlp.hcoref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.*;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.Properties;

public class Sentiment {
    public static void main(String[] args) throws Exception {
        Annotation document = new Annotation("Just had a chicken burger. But. Oh. My. God. Amazing. No idea what Spatinental is but I need to book a trip to the Spatinent. Yum! Pricey for a burger and salad- $13. But so good.");
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        pipeline.annotate(document);
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) 
        {
            System.out.println("---");
            System.out.println(sentence);
            System.out.println(sentence.get(SentimentAnnotatedTree.class));
            System.out.println(sentence.get(SentimentClass.class));
        }
    }
}