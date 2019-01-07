package com.hiltonroscoe.mdreportext;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import com.nomagic.magicreport.engine.Tool;

import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Supports Natural Language Processings techniques to aid in the generation of
 * a semantically linked glossary.
 */
public class NLPLanguage extends Tool {

  private static final long serialVersionUID = 1L;
  private static StanfordCoreNLP _pipeline;

  /**
   * Wrapper around the pipeline, to keep a warm instance around. Because the
   * pipeline instance is static, the mappingFile parameter will non always be
   * honored.
   * 
   * @param mappingFile The mapping file path, for NER
   * @return A Stanford CoreNLP pipeline
   */
  public static StanfordCoreNLP getPipeline(String mappingFile) {
    if (_pipeline != null) {
      return _pipeline;
    } else {
      createPipeline(mappingFile);
      return _pipeline;
    }
  }

  /**
   * Creates a pipeline for repeated use
   * 
   * @param mappingFile The mapping file path, for NER
   */
  private static void createPipeline(String mappingFile) {
    // set up pipeline properties
    Properties props = new Properties();
    // set the list of annotators to run
    props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,ner");// ,coref,kbp,quote");

    // disable statistical models
    // REMOVE?
    props.setProperty("pos.model",
        "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
    // WE CAN'T REMOVE BC OF A BUG IN CORENLP which breaks mentions!!
    props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
    props.setProperty("ner.applyFineGrained", "false");
    // to avoid loading more models
    props.setProperty("ner.useSUTime", "false");
    props.setProperty("ner.additional.regexner.mapping", mappingFile);
    props.setProperty("ner.additional.regexner.ignorecase", "true");
    // normalized is a place to put our hyperlink target (i.e. the term name)
    props.setProperty("ner.additional.regexner.mapping.header", "pattern,ner,normalized,priority");
    props.setProperty("ner.additional.regexner.mapping.field.normalized",
        "edu.stanford.nlp.ling.CoreAnnotations$NormalizedNamedEntityTagAnnotation");
    props.setProperty("ner.buildEntityMentions", "true");
    // build pipeline, this is an expensive operation!
    _pipeline = new StanfordCoreNLP(props);
  }

  /**
   * Filters the list of mentions, so that they appear only in desired contexts.
   * 
   * @param termMentions A list of mentions
   * @param currentTerm  The name of the current term being annotated
   * @return A permitted list of mentions
   */
  public static List<CoreEntityMention> getPermittedMentions(List<CoreEntityMention> termMentions, String currentTerm,
      String[] skipWords) {
    // controls whether we allow multiple mentions
    boolean firstMentionsOnly = true;

    // the entity mentions are our terms matched in definition
    // need to detect them as we write out the sentence.
    List<CoreEntityMention> permittedMentions = new LinkedList<CoreEntityMention>();
    outerLoop:
    for (CoreEntityMention termMention : termMentions) {
      // we are assuming the list is in reading order!
      // only include terms we tagged (i.e. ignore statistical models)
      if (termMention.entityType() == "GLOSSARY_TERM") {

        boolean disallowSelfReference = true;
        if (disallowSelfReference) {
          // don't link to the term we are annotating!
          if (currentTerm.equals(termMention.text())) {
            continue;
          }
        }

        if (firstMentionsOnly) {
          // check if we don't already have a mention.
          if (permittedMentions.stream().anyMatch(p -> p.text().equals(termMention.text()))) {
            continue;
          }
        }

        if (skipWords != null) {
          for (String skipTerm : skipWords) {
            if (termMention.coreMap()
                    .get(NormalizedNamedEntityTagAnnotation.class).toString().equals(skipTerm)) {
              continue outerLoop;
            }
          }
        }

        // if we got this far, add the term
        permittedMentions.add(termMention);

      }
    }
    return permittedMentions;
  }

  /**
   * Entry point for MagicDraw report
   * 
   * @param text        The corpus to annotate
   * @param currentTerm The current term under annotation
   * @return The annotated string, in GFM form
   */
  public static String runNLP(String text, String currentTerm, String mappingFile, List<String> skipWords) {
    StanfordCoreNLP pipeline = getPipeline(mappingFile);
    // create a document object
    CoreDocument document = new CoreDocument(text);
    // annnotate the document
    pipeline.annotate(document);

    List<CoreEntityMention> termMentions = document.entityMentions();
    List<CoreEntityMention> permittedMentions = getPermittedMentions(termMentions, currentTerm,
        skipWords.toArray(new String[0]));

    // iterate over the permitted mentions, matching them as we go
    Iterator<CoreEntityMention> permittedMentionsIt = permittedMentions.iterator();
    CoreEntityMention mentionMatchTarget = null;
    if (permittedMentionsIt.hasNext()) {
      mentionMatchTarget = permittedMentionsIt.next();
    }
    // create a stream we can read through
    StringWriter definitionOutputStream = new StringWriter();
    try (StringReader definitionInputStream = new StringReader(document.text())) {
      int currentChar;
      int pos = 0;
      while ((currentChar = definitionInputStream.read()) != -1) {
        if (mentionMatchTarget != null) {
          try {
            if (pos == mentionMatchTarget.charOffsets().first()) {
              // write bracket
              definitionOutputStream.write("[");
            } else if (pos == mentionMatchTarget.charOffsets().second()) {
              // write bracket
              definitionOutputStream.write("]");
              if (mentionMatchTarget.coreMap().containsKey(NormalizedNamedEntityTagAnnotation.class)) {
                definitionOutputStream.write("(#" + mentionMatchTarget.coreMap()
                    .get(NormalizedNamedEntityTagAnnotation.class).toString().replaceAll("\\s+", "-") + ")");
              }
              mentionMatchTarget = permittedMentionsIt.next();
            }
          } catch (NoSuchElementException e) {
          }
        }
        definitionOutputStream.write(currentChar);
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
