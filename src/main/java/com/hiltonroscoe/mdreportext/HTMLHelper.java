package com.hiltonroscoe.mdreportext;

import com.overzealous.remark.Remark;
import com.overzealous.remark.Options;
import com.nomagic.magicreport.engine.Tool;
import java.io.*;

public class HTMLHelper extends Tool {

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