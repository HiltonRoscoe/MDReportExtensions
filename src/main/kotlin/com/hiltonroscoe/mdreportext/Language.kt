package com.hiltonroscoe.mdreportext

import java.util.ArrayList

import com.hiltonroscoe.nlp.Stemmer
import com.nomagic.magicreport.engine.Tool

import edu.stanford.nlp.process.Morphology
import edu.stanford.nlp.simple.Document
import edu.stanford.nlp.simple.Sentence

/**
 * Old language subroutines used for initial versions of the glossary. Please
 * use NLPLanguage instead.
 */
class Language : Tool() {
    companion object {
        private val serialVersionUID = 1L

        /**
         * Stems a word
         *
         * @param s The word to stem
         * @return The stem of the word
         */
        @JvmStatic
        fun stem(s: String): String {
            val l = Stemmer()
            return l.stem(s)
        }

        /**
         * REMOVE?
         */
        fun lemmaize(s: String): String {
            val morphology = Morphology()
            return morphology.stem(s)
        }
        @JvmStatic
        fun getWords(s: String): Array<String> {
            val doc = Document(s)
            // a list of strings in the class' name
            return doc.sentence(0).words().toTypedArray<String>()
        }

        /**
         * Return an array of lemmas found in a String
         *
         * @param s The String to scan
         * @return An array of lemmas
         */
        @JvmStatic
        fun getLemmas(s: String): Array<String> {
            // still using simple API
            // Add an article to the sentence. This fixes an issue with POS tags.
            val doc = Document("a $s")
            // will break if periods are in the term (they shouldn't)
            val rawLemmas = doc.sentence(0).lemmas()
            // remove the "a" that we added to force correct POS tagging
            val lemmas = rawLemmas.subList(1, rawLemmas.size)
            return lemmas.toTypedArray<String>()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val lemmas = getLemmas("voting system")
            for (l in lemmas) {
                println(l)
            }
            println(findClassUsage("the ballot, being unreadable by a scanner.", "ballot", true))
        }

        /**
         * Used by previous versions of the glossary to detect matching terms
         *
         * @param documentation The document to scan
         * @param className     The class to look for
         * @param stem          If true, uses stemming instead of lemmaization
         * @return An array of matched terms
         */
        fun findClassUsage(documentation: String, className: String, stem: Boolean): Array<String> {
            // Create a document. No computation is done yet.
            val doc = Document(documentation)
            // a list of strings in the class' name
            val matchedPhrases = ArrayList<String>()
            val classStrings: Array<String>
            if (!stem)
                classStrings = Document(className).sentence(0).lemmas().toTypedArray<String>()
            else
                classStrings = Document(className).sentence(0).words().toTypedArray<String>()
            // iterate over sentences
            for (sent in doc.sentences()) {
                val docStrings: Array<String>
                if (!stem)
                // get all lemmas
                    docStrings = sent.lemmas().toTypedArray<String>()
                else
                    docStrings = sent.words().toTypedArray<String>()
                for (l in docStrings.indices) {
                    val matchingPhrase = ArrayList<String>()
                    // try to match class name at current position (l) of document
                    for (m in classStrings.indices) {
                        val matches: Boolean
                        if (stem) {
                            val s = com.hiltonroscoe.nlp.Stemmer()
                            matches = s.stem(classStrings[m]) == s.stem(docStrings[l + m])
                        } else {
                            // compare lemmas at current position
                            matches = classStrings[m] == docStrings[l + m]
                        }
                        if (matches) {
                            matchingPhrase.add(sent.word(l + m))
                            if (m == classStrings.size - 1) {
                                // consumed!
                                matchedPhrases.add(matchingPhrase.joinToString(" "))
                                // if we cared about speed, we would fast forward
                            }
                            // continue to scan
                        } else {
                            // break early to save time
                            break
                        }
                    }
                }
            }
            return matchedPhrases.toTypedArray<String>()
        }
    }
}
