package com.hiltonroscoe.mdreportext;

import java.util.ArrayList;
import java.util.List;

import com.hiltonroscoe.nlp.Stemmer;
import com.nomagic.magicreport.engine.Tool;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;

/**
 * Old language subroutines used for initial versions of the glossary. Please
 * use NLPLanguage instead.
 */
public class Language extends Tool {
    private static final long serialVersionUID = 1L;

    /**
     * Stems a word
     * 
     * @param s The word to stem
     * @return The stem of the word
     */
    public static String stem(String s) {
        Stemmer l = new Stemmer();
        return l.stem(s);
    }

    /**
     * REMOVE?
     */
    public static String lemmaize(String s) {
        Morphology morphology = new Morphology();
        return morphology.stem(s);
    }

    /**
     * Return an array of lemmas found in a String
     * 
     * @param s The String to scan
     * @return An array of lemmas
     */
    public static String[] getLemmas(String s) {
        // still using simple API
        // Add an article to the sentence. This fixes an issue with POS tags.
        Document doc = new Document("a " + s);
        // will break if periods are in the term (they shouldn't)
        List<String> rawLemmas = doc.sentence(0).lemmas();
        // remove the "a" that we added to force correct POS tagging
        List<String> lemmas = rawLemmas.subList(1, rawLemmas.size());
        return lemmas.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String[] lemmas = getLemmas("voting system");
        for (String l : lemmas) {
            System.out.println(l);
        }
        System.out.println(findClassUsage("the ballot, being unreadable by a scanner.", "ballot", true));
    }

    /**
     * Used by previous versions of the glossary to detect matching terms
     * 
     * @param documentation The document to scan
     * @param className     The class to look for
     * @param stem          If true, uses stemming instead of lemmaization
     * @return An array of matched terms
     */
    public static String[] findClassUsage(String documentation, String className, boolean stem) {
        // Create a document. No computation is done yet.
        Document doc = new Document(documentation);
        // a list of strings in the class' name
        List<String> matchedPhrases = new ArrayList<String>();
        String[] classStrings;
        if (!stem)
            classStrings = new Document(className).sentence(0).lemmas().toArray(new String[0]);
        else
            classStrings = new Document(className).sentence(0).words().toArray(new String[0]);
        // iterate over sentences
        for (Sentence sent : doc.sentences()) {
            String[] docStrings;
            if (!stem)
                // get all lemmas
                docStrings = sent.lemmas().toArray(new String[0]);
            else
                docStrings = sent.words().toArray(new String[0]);
            for (int l = 0; l < docStrings.length; l++) {
                List<String> matchingPhrase = new ArrayList<String>();
                // try to match class name at current position (l) of document
                for (int m = 0; m < classStrings.length; m++) {
                    boolean matches;
                    if (stem) {
                        com.hiltonroscoe.nlp.Stemmer s = new com.hiltonroscoe.nlp.Stemmer();
                        matches = s.stem(classStrings[m]).equals(s.stem(docStrings[l + m]));
                    } else {
                        // compare lemmas at current position
                        matches = (classStrings[m]).equals(docStrings[l + m]);
                    }
                    if (matches) {
                        matchingPhrase.add(sent.word(l + m));
                        if (m == classStrings.length - 1) {
                            // consumed!
                            matchedPhrases.add(String.join(" ", matchingPhrase));
                            // if we cared about speed, we would fast forward
                        }
                        // continue to scan
                    } else {
                        // break early to save time
                        break;
                    }
                }
            }
        }
        return matchedPhrases.toArray(new String[0]);
    }
}
