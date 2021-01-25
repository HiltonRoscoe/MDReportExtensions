package com.hiltonroscoe.mdreportext

import com.nomagic.magicreport.engine.Tool
import com.overzealous.remark.Options
import com.overzealous.remark.Remark

/**
 * Provides conversion utilities from HTML to Markdown, with additional
 * facilities to handle MagicDraw's uri scheme.
 */
class HTMLHelper : Tool() {
    companion object {
        private val serialVersionUID = 1L

        /**
         * Converts an HTML fragment into an equivilent Markdown version.
         *
         * @param htmlString An HTML fragment
         * @return A GFM-style Markdown fragment
         */
        fun htmlToMarkdown(htmlString: String): String {
            try {
                val options = Options.github()
                options.inlineLinks = true
                val remark = Remark(options)
                val markdown = remark.convertFragment(htmlString)
                return markdown
            } catch (e: Error) {
                return e.toString()
            }

        }

        /**
         * Converts an MagicDraw HTML fragment into an equivilent Markdown version.
         *
         * @param htmlString An HTML fragment
         * @return A GFM-style Markdown fragment
         */
        @JvmStatic
        fun mdhtmlToMarkdown(htmlString: String): String {
            try {
                val options = Options.github()
                options.inlineLinks = true
                val remark = Remark(options)
                // change mdel to http so the remark tool doesn't remove the link
                val httpString = htmlString.replace("mdel://".toRegex(), "http://bookmark")
                val markdown = remark.convertFragment(httpString)
                // convert mdel links to bookmarks
                val bookmarkString = markdown.replace("http://bookmark".toRegex(), "#")
                return bookmarkString
            } catch (e: Error) {
                return e.toString()
            }

        }
    }

}