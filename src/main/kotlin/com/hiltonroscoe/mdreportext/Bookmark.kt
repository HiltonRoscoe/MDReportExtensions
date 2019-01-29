package com.hiltonroscoe.mdreportext

import com.nomagic.magicreport.engine.Tool

/**
 * A Markdown version of the MagicDraw Bookmark tool. Allows for easy conversion
 * of existing Word templates.
 */
class Bookmark : Tool() {
    companion object {
        private val serialVersionUID = 1L

        /**
         * Creates a link to an existing bookmark
         *
         * @param bookmarkId The target bookmarkId
         * @param content    The content of the anchor
         * @return A Markdown fragment
         */
        fun open(bookmarkId: String, content: String): String {
            return "[$content]($bookmarkId)"
        }

        /**
         * Creates a anchor for referencing
         *
         * @param bookmarkId     The target bookmarkId
         * @param bookmarkObject The name of the bookmark
         * @return
         */
        fun create(bookmarkId: String, bookmarkObject: String): String {
            return "<a name=\"$bookmarkId\"></a>$bookmarkObject"
        }
    }
}
