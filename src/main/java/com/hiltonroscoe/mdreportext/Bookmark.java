package com.hiltonroscoe.mdreportext;

import com.nomagic.magicreport.engine.Tool;

public class Bookmark extends Tool {
    public static String open(String bookmarkId, String content) {
        return "[" + content + "](" + bookmarkId + ")";
    }
    public static String create(String bookmarkId, String bookmarkObject) {
        return "<a name=\"" + bookmarkId + "\"></a>" + bookmarkObject;
    }
}
