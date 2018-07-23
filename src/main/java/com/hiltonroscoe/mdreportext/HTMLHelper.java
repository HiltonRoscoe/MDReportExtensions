package com.hiltonroscoe.mdreportext;

import com.overzealous.remark.Remark;
import com.overzealous.remark.Options;
import com.nomagic.magicreport.engine.Tool;

public class HTMLHelper extends Tool {
    private static Remark remark = new Remark(Options.github());

    public static String htmlToMarkdown(String htmlString) {
        try {
            String markdown = remark.convertFragment(htmlString);
            return markdown;
        } catch (Error e) {
            return e.toString();
        }
    }
}