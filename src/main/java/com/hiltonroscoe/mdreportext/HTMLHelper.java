package com.hiltonroscoe.mdreportext;

import com.nomagic.magicreport.engine.Tool;
import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;

/**
 * Provides conversion utilities from HTML to Markdown, with additional
 * facilities to handle MagicDraw's uri scheme.
 */
public class HTMLHelper extends Tool {
    private static final long serialVersionUID = 1L;

    /**
     * Converts an HTML fragment into an equivilent Markdown version.
     * 
     * @param htmlString An HTML fragment
     * @return A GFM-style Markdown fragment
     */
    public static String htmlToMarkdown(String htmlString) {
        try {
            Options options = Options.github();
            options.inlineLinks = true;
            Remark remark = new Remark(options);
            String markdown = remark.convertFragment(htmlString);
            return markdown;
        } catch (Error e) {
            return e.toString();
        }
    }
    /**
     * Converts an MagicDraw HTML fragment into an equivilent Markdown version.
     * 
     * @param htmlString An HTML fragment
     * @return A GFM-style Markdown fragment
     */
    public static String mdhtmlToMarkdown(String htmlString) {
        try {
            Options options = Options.github();
            options.inlineLinks = true;
            Remark remark = new Remark(options);
            // change mdel to http so the remark tool doesn't remove the link
            String httpString = htmlString.replaceAll("mdel://", "http://bookmark");
            String markdown = remark.convertFragment(httpString);
            // convert mdel links to bookmarks
            String bookmarkString = markdown.replaceAll("http://bookmark", "#");
            return bookmarkString;
        } catch (Error e) {
            return e.toString();
        }
    }

}