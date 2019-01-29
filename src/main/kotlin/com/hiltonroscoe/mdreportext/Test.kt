package com.hiltonroscoe.mdreportext

import java.util.ArrayList

/**
 * Harness for testing the JAR prior to deployment.
 */
object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val text = "A set of marked ballots with a predetermined outcome. Used for logic and accuracy testing of a voting system."
        val skipTerms = ArrayList<String>()
        skipTerms.add("marked ballot")
        println(NLPLanguage.runNLP(text, "ballot style", "mapping.txt", skipTerms))
    }
}