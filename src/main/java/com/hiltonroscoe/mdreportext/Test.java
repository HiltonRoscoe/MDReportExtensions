package com.hiltonroscoe.mdreportext;
/**
 * Harness for testing the JAR prior to deployment.
 */
public class Test {
    public static void main(String[] args) {
        String text = "Programmed device that creates credentials necessary to begin a voting session using a specific ballot style.";
        System.out.println(NLPLanguage.runNLP(text, "ballot style", "mapping.txt"));
    }
}