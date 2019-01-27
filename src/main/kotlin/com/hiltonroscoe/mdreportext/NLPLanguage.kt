package com.hiltonroscoe.mdreportext

import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.util.LinkedList
import java.util.NoSuchElementException
import java.util.Properties

import com.nomagic.magicreport.engine.Tool

import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation
import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.CoreEntityMention
import edu.stanford.nlp.pipeline.StanfordCoreNLP

/**
 * Supports Natural Language Processings techniques to aid in the generation of
 * a semantically linked glossary.
 */
class NLPLanguage : Tool() {
    companion object {

        private val serialVersionUID = 1L
        private var _pipeline: StanfordCoreNLP? = null

        /**
         * Wrapper around the pipeline, to keep a warm instance around. Because the
         * pipeline instance is static, the mappingFile parameter will non always be
         * honored.
         *
         * @param mappingFile The mapping file path, for NER
         * @return A Stanford CoreNLP pipeline
         */
        fun getPipeline(mappingFile: String): StanfordCoreNLP? {
            if (_pipeline != null) {
                return _pipeline
            } else {
                createPipeline(mappingFile)
                return _pipeline
            }
        }

        /**
         * Creates a pipeline for repeated use
         *
         * @param mappingFile The mapping file path, for NER
         */
        private fun createPipeline(mappingFile: String) {
            // set up pipeline properties
            val props = Properties()
            // set the list of annotators to run
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,ner")// ,coref,kbp,quote");

            // disable statistical models
            // REMOVE?
            props.setProperty("pos.model",
                    "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger")
            // WE CAN'T REMOVE BC OF A BUG IN CORENLP which breaks mentions!!
            props.setProperty("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz")
            props.setProperty("ner.applyFineGrained", "false")
            // to avoid loading more models
            props.setProperty("ner.useSUTime", "false")
            props.setProperty("ner.additional.regexner.mapping", mappingFile)
            props.setProperty("ner.additional.regexner.ignorecase", "true")
            // normalized is a place to put our hyperlink target (i.e. the term name)
            props.setProperty("ner.additional.regexner.mapping.header", "pattern,ner,normalized,priority")
            props.setProperty("ner.additional.regexner.mapping.field.normalized",
                    "edu.stanford.nlp.ling.CoreAnnotations\$NormalizedNamedEntityTagAnnotation")
            props.setProperty("ner.buildEntityMentions", "true")
            // build pipeline, this is an expensive operation!
            _pipeline = StanfordCoreNLP(props)
        }

        /**
         * Filters the list of mentions, so that they appear only in desired contexts.
         *
         * @param termMentions A list of mentions
         * @param currentTerm  The name of the current term being annotated
         * @return A permitted list of mentions
         */
        fun getPermittedMentions(termMentions: List<CoreEntityMention>, currentTerm: String,
                                 skipWords: Array<String>?): List<CoreEntityMention> {
            // controls whether we allow multiple mentions
            val firstMentionsOnly = true

            // the entity mentions are our terms matched in definition
            // need to detect them as we write out the sentence.
            val permittedMentions = LinkedList<CoreEntityMention>()
            outerLoop@ for (termMention in termMentions) {
                // we are assuming the list is in reading order!
                // only include terms we tagged (i.e. ignore statistical models)
                if (termMention.entityType() === "GLOSSARY_TERM") {
                    val normalizedTerm = termMention.coreMap().get<String>(NormalizedNamedEntityTagAnnotation::class.java)?.toString() ?: println("${termMention.text()} not found!")
                    val disallowSelfReference = true
                    if (disallowSelfReference) {
                        // don't link to the term we are annotating!
                        if (currentTerm == normalizedTerm) {
                            continue
                        }
                    }

                    if (firstMentionsOnly) {
                        // check if we don't already have a mention.
                        // base first mentions based on underlying term, i.e. the normalized name entity
                        // this means we won't link the term twice even if different synonyms are used
                        if (permittedMentions.any { p -> p.coreMap().get<String>(NormalizedNamedEntityTagAnnotation::class.java).toString() == normalizedTerm }) {
                            continue
                        }
                    }

                    if (skipWords != null) {
                        for (skipTerm in skipWords) {
                            if (normalizedTerm == skipTerm) {
                                continue@outerLoop
                            }
                        }
                    }

                    // if we got this far, add the term
                    permittedMentions.add(termMention)

                }
            }
            return permittedMentions
        }

        /**
         * Entry point for MagicDraw report
         *
         * @param text        The corpus to annotate
         * @param currentTerm The current term under annotation
         * @return The annotated string, in GFM form
         */
        @JvmStatic
        fun runNLP(text: String, currentTerm: String, mappingFile: String, skipWords: List<String>): String {
            val pipeline = getPipeline(mappingFile)
            // create a document object
            val document = CoreDocument(text)
            // annnotate the document
            pipeline!!.annotate(document)

            val termMentions = document.entityMentions()
            val permittedMentions = getPermittedMentions(termMentions, currentTerm,
                    skipWords.toTypedArray<String>())

            // iterate over the permitted mentions, matching them as we go
            val permittedMentionsIt = permittedMentions.iterator()
            var mentionMatchTarget: CoreEntityMention? = null
            if (permittedMentionsIt.hasNext()) {
                mentionMatchTarget = permittedMentionsIt.next()
            }
            // create a stream we can read through
            val definitionOutputStream = StringWriter()
            try {
                StringReader(document.text()).use { definitionInputStream ->
                    var currentChar: Int
                    var pos = 0
                    while (true) {
                        currentChar = definitionInputStream.read()
                        if(currentChar == -1)
                          break
                        if (mentionMatchTarget != null) {
                            try {
                                if (pos == mentionMatchTarget!!.charOffsets().first()) {
                                    // write bracket
                                    definitionOutputStream.write("[")
                                } else if (pos == mentionMatchTarget!!.charOffsets().second()) {
                                    // write bracket
                                    definitionOutputStream.write("]")
                                    if (mentionMatchTarget!!.coreMap().containsKey<String>(NormalizedNamedEntityTagAnnotation::class.java)) {
                                        definitionOutputStream.write("(#" + mentionMatchTarget!!.coreMap()
                                                .get<String>(NormalizedNamedEntityTagAnnotation::class.java).toString().replace("\\s+".toRegex(), "-") + ")")
                                    }
                                    mentionMatchTarget = permittedMentionsIt.next()
                                }
                            } catch (e: NoSuchElementException) {
                            }

                        }
                        definitionOutputStream.write(currentChar)
                        pos++

                    }
                    definitionOutputStream.close()
                    // return to end user
                    return definitionOutputStream.toString()

                }
            } catch (e: IOException) {
                return e.toString()
            }

        }
    }

}
