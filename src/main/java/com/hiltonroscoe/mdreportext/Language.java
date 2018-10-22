package com.hiltonroscoe.mdreportext;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.Stemmer;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;

import com.nomagic.magicreport.engine.Tool;

import org.apache.xalan.templates.ElemSort;

import edu.stanford.nlp.simple.*;

public class Language extends Tool {
    public static String stem(String s) {
        Stemmer l = new Stemmer();
        return l.stem(s);
    }

    public static void main(String[] args) {
        System.out.println(findClassUsage(
                "the ballot, being unreadable by a scanner.",
                "ballot",true));
    }

    public static String findClassUsage(String documentation, String className, boolean stem) {
        // Create a document. No computation is done yet.
        Document doc = new Document(documentation);
        String[] classLemmas;
        if (!stem)
            classLemmas = new Document(className).sentence(0).lemmas().toArray(new String[0]);
        else
            classLemmas = new Document(className).sentence(0).words().toArray(new String[0]);
        // foreach(classLemmas in classes) {
        for (Sentence sent : doc.sentences()) { // Will iterate over two sentences
            // get all lemmas
            String[] docLemmas;
            if (!stem)
                docLemmas = sent.lemmas().toArray(new String[0]);
            else
                docLemmas = sent.words().toArray(new String[0]);
            for (int l = 0; l < docLemmas.length; l++) {
                List<String> wordToReturn = new ArrayList<String>();
                for (int m = 0; m < classLemmas.length; m++) {
                    // compare lemmas at current position
                    boolean matches;
                    if (stem) {
                        com.hiltonroscoe.nlp.Stemmer s = new com.hiltonroscoe.nlp.Stemmer();
                        matches = s.stem(classLemmas[m]).equals(s.stem(docLemmas[l + m]));
                    } else {
                        matches = (classLemmas[m]).equals(docLemmas[l + m]);
                    }
                    if (matches) {
                        wordToReturn.add(sent.word(l + m));
                        if (m == classLemmas.length - 1) {
                            // consumed!
                            // return the matched string, can use existing regex
                            return String.join(" ", wordToReturn);
                        }
                        // continue to scan
                    } else {
                        // break early to save time
                        break;
                    }
                }
            }
        }
        return "NO MATCH";
    }
}
