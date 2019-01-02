package com.hiltonroscoe.mdreportext;

import com.nomagic.magicreport.engine.Tool;

/**
 * A Markdown version of the MagicDraw Bookmark tool. Allows for easy conversion
 * of existing Word templates.
 */
public class Bookmark extends Tool {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a link to an existing bookmark
     * 
     * @param bookmarkId The target bookmarkId
     * @param content    The content of the anchor
     * @return A Markdown fragment
     */
    public static String open(String bookmarkId, String content) {
        return "[" + content + "](" + bookmarkId + ")";
    }

    /**
     * Creates a anchor for referencing
     * 
     * @param bookmarkId     The target bookmarkId
     * @param bookmarkObject The name of the bookmark
     * @return
     */
    public static String create(String bookmarkId, String bookmarkObject) {
        return "<a name=\"" + bookmarkId + "\"></a>" + bookmarkObject;
    }
}
