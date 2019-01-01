package com.hiltonroscoe.mdreportext;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import com.nomagic.magicreport.engine.Tool;

public class NLPLanguage extends Tool {

  private static StanfordCoreNLP _pipeline;

  public static StanfordCoreNLP getPipeline() {
    if (_pipeline != null) {
      return _pipeline;
    } else {
      createPipeline();
      return _pipeline;
    }
  }

  private static void createPipeline() {
    // set up pipeline properties
    Properties props = new Properties();
    // set the list of annotators to run
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,ner");// ,coref,kbp,quote");
    // set a property for an annotator, in this case the coref annotator is being
    // set to use the neural algorithm
    // props.setProperty("coref.algorithm", "neural");

    // set properties for TERM matching (i.e. NER)

    // disable statistical models
    // WE CAN'T BC OF A BUG IN CORENLP which breaks mentions!!
    props.setProperty("pos.verbose", "true");
    props.setProperty("pos.model",
        "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
    props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    props.setProperty("ner.applyFineGrained", "false");
    // to avoid loading more models
    props.setProperty("ner.useSUTime", "false");
    props.setProperty("ner.additional.regexner.mapping", "mapping.txt");
    props.setProperty("ner.additional.regexner.ignorecase", "true");
    // normalized is a place to put our hyperlink target (i.e. the term name)
    props.setProperty("ner.additional.regexner.mapping.header", "pattern,ner,normalized,priority");
    props.setProperty("ner.additional.regexner.mapping.field.normalized",
        "edu.stanford.nlp.ling.CoreAnnotations$NormalizedNamedEntityTagAnnotation");
    // to fix exception, when running regxner
    props.setProperty("ner.buildEntityMentions", "true");
    // build pipeline
    _pipeline = new StanfordCoreNLP(props);
  }

  public static void main(String[] args) {
    String text = "Programmed device that creates credentials necessary to begin a voting session using a specific ballot style. Oh how I do love ballot style.";
    System.out.println(runNLP(text,"ballot style"));
  }
/**
 * 
 * @param text The corpus to annotate
 * @param currentTerm The current term under annotation
 * @return
 */
  public static String runNLP(String text, String currentTerm) {
    StanfordCoreNLP pipeline = getPipeline();
    // create a document object
    CoreDocument document = new CoreDocument(text);
    // annnotate the document
    pipeline.annotate(document);

    // after annotation, we need to reconstruct our current term,
    // with all the goodness

    // controls whether we allow multiple mentions
    boolean firstMentions = true;

    // for (CoreSentence sentence : document.sentences()) {
    // the entity mentions are our terms matched in definition
    // need to detect them as we write out the sentence.
    List<CoreEntityMention> permittedMentions = new LinkedList<CoreEntityMention>();
    List<CoreEntityMention> termMentions = document.entityMentions();
    for (CoreEntityMention termMention : termMentions) {
      // System.out.println("[NER] " + termMention);
      // we are assuming the list is in reading order!
      boolean disallowSelfReference = true;
      if(disallowSelfReference){
        // don't link to the term we are annotating!
        if(currentTerm.equals(termMention.text())){
          continue;
        }
      }
      
      if (firstMentions) {
        // check if we don't already have a mention.
        if (permittedMentions.stream().anyMatch(p -> p.text().equals(termMention.text()))) {
          // System.out.println("[NER] already matched " + termMention.text());
        } else {
          permittedMentions.add(termMention);
        }
      }
    }

    // DEBUG SECTION
    // for (CoreLabel token : document.tokens()) {
    //   System.out.println(token.word() + "\t" + token.beginPosition() + "\t" + token.endPosition());
    // }
    // System.out.println("permitted mentions " + permittedMentions);
    Iterator<CoreEntityMention> permittedMentionsIt = permittedMentions.iterator();
    CoreEntityMention mentionMatchTarget = permittedMentionsIt.next();
    // create a stream we can read through
    StringWriter definitionOutputStream = new StringWriter();
    try (StringReader definitionInputStream = new StringReader(document.text())) {
      int char2;
      int pos = 0;
      while ((char2 = definitionInputStream.read()) != -1) {
        try {
          if (pos == mentionMatchTarget.charOffsets().first()) {
            // write bracket
            definitionOutputStream.write("[");
          } else if (pos == mentionMatchTarget.charOffsets().second()) {
            // write bracket
            definitionOutputStream.write("]");
            if (mentionMatchTarget.coreMap().containsKey(NormalizedNamedEntityTagAnnotation.class)) {
              definitionOutputStream.write(
                  "(#" + mentionMatchTarget.coreMap().get(NormalizedNamedEntityTagAnnotation.class).toString().replaceAll("\\s+","") + ")");
            }
            mentionMatchTarget = permittedMentionsIt.next();
          }
        } catch (NoSuchElementException e) {
        }
        definitionOutputStream.write(char2);
        pos++;

      }
      definitionOutputStream.close();
      // return to end user
      return definitionOutputStream.toString();

    } catch (IOException e) {
      return e.toString();
    }

  }

}
