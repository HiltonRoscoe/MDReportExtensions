package com.hiltonroscoe.mdreportext;

import java.util.ArrayList;

/**
 * Harness for testing the JAR prior to deployment.
 */
public class Test {
    public static void main(String[] args) {
        String text = "A set of marked ballots with a predetermined outcome. Used for logic and accuracy testing of a voting system.";
        ArrayList<String> skipTerms = new ArrayList<String>();
        skipTerms.add("marked ballot");
        System.out.println(NLPLanguage.runNLP(text, "ballot style", "mapping.txt", skipTerms));
    }
}