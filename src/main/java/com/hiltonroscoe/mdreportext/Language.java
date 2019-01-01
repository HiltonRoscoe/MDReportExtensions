package com.hiltonroscoe.mdreportext;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;

import com.hiltonroscoe.nlp.Stemmer;
import com.nomagic.magicreport.engine.Tool;

import org.apache.xalan.templates.ElemSort;

import edu.stanford.nlp.simple.*;

public class Language extends Tool {
    public static String stem(String s) {
        Stemmer l = new Stemmer();
        return l.stem(s);
    }

    public static String lemmaize(String s) {
        Morphology morphology = new Morphology();
        return morphology.stem(s);
    }

    public static String[] getLemmas(String s) {
        // still using simple API
        Document doc = new Document(s);
        return doc.sentence(0).lemmas().toArray(new String[0]);
    }

    public static void main(String[] args) {
        System.out.println(findClassUsage("the ballot, being unreadable by a scanner.", "ballot", true));
    }

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
