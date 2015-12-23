
import java.io.*;
import java.util.*;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

/** This class demonstrates building and using a Stanford CoreNLP pipeline. */
public class Sentiment2 {

  /** Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]] */
  public static void main(String[] args) throws IOException {
    // set up optional output files
    PrintWriter out;
    if (args.length > 1) {
      out = new PrintWriter(args[1]);
    } else {
      out = new PrintWriter(System.out);
    }
    PrintWriter xmlOut = null;
    if (args.length > 2) {
      xmlOut = new PrintWriter(args[2]);
    }

    // Create a CoreNLP pipeline. To build the default pipeline, you can just use:
    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    // Here's a more complex setup example:
    //   Properties props = new Properties();
    //   props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
    //   props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    //   props.put("ner.applyNumericClassifiers", "false");
    //   StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // Add in sentiment
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");

    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
    Annotation annotation;
    if (args.length > 0) {
      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
    } else {
      annotation = new Annotation("This is definitely not a book to be judged by the cover. This place looks kind of dingy, and looks combined the B rating on the front window had my wife saying I had read all the amazing reviews on Yelp so I had to try it. The guy behind the counter doesn't seem to speak much English, and they only have VEGITARIAN options (so if you are looking for meat head somewhere else) but the food was AMAZING!!!!!!!! I prefer vegetarian dishes anyways so this was a plus for me. \n\n The dinning area leaves a little to be desired, especially if you are looking to dine with your family. However it kind of fits the personality of the shop. Its all about the food and it is a superstar. If you are looking for Indian in NYC this is the place; delicious offerings and a VERY reasonable price. I will be back for sure!");
    }

    // run all the selected Annotators on this text
    pipeline.annotate(annotation);

    // this prints out the results of sentence analysis to file(s) in good formats
    pipeline.prettyPrint(annotation, out);
    if (xmlOut != null) {
      pipeline.xmlPrint(annotation, xmlOut);
    }

    // Access the Annotation in code
    // The toString() method on an Annotation just prints the text of the Annotation
    // But you can see what is in it with other methods like toShorterString()
    out.println();
    out.println("The top level annotation");
    out.println(annotation.toShorterString());
    out.println();

    // An Annotation is a Map with Class keys for the linguistic analysis types.
    // You can get and use the various analyses individually.
    // For instance, this gets the parse tree of the first sentence in the text.
    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
    if (sentences != null && ! sentences.isEmpty()) {
      CoreMap sentence = sentences.get(0);
      out.println("The keys of the first sentence's CoreMap are:");
      out.println(sentence.keySet());
      out.println();
      out.println("The first sentence is:");
      out.println(sentence.toShorterString());
      out.println();
      out.println("The first sentence tokens are:");
      for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        out.println(token.toShorterString());
      }
      Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      out.println();
      out.println("The first sentence parse tree is:");
      tree.pennPrint(out);
      out.println();
      out.println("The first sentence basic dependencies are:");
      out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
      out.println("The first sentence collapsed, CC-processed dependencies are:");
      SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
      out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

      // Access coreference. In the coreference link graph,
      // each chain stores a set of mentions that co-refer with each other,
      // along with a method for getting the most representative mention.
      // Both sentence and token offsets start at 1!
      out.println("Coreference information");
      Map<Integer, CorefChain> corefChains =
          annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
      if (corefChains == null) { return; }
      for (Map.Entry<Integer,CorefChain> entry: corefChains.entrySet()) {
        out.println("Chain " + entry.getKey() + " ");
        for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
          // We need to subtract one since the indices count from 1 but the Lists start from 0
          List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
          // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
          out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
                  ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
        }
      }
      out.println();

      out.println("The first sentence overall sentiment rating is " + sentence.get(SentimentCoreAnnotations.SentimentClass.class));
    }
    IOUtils.closeIgnoringExceptions(out);
    IOUtils.closeIgnoringExceptions(xmlOut);
  }

}
