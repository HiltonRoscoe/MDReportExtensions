package com.hiltonroscoe.mdreportext;

import com.overzealous.remark.Remark;
import com.overzealous.remark.Options;
import com.nomagic.magicreport.engine.Tool;
import java.io.*;
import javax.script.*;

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
            String httpString = htmlString.replaceAll("mdel://","http://bookmark");
            String markdown = remark.convertFragment(httpString);
            // convert mdel links to bookmarks
            String bookmarkString = markdown.replaceAll("http://bookmark", "#");
            return bookmarkString;
        } catch (Error e) {
            return e.toString();
        }
    }

    public static String turnDown(String htmlString) {
        try {
            ClassLoader classLoader = HTMLHelper.class.getClassLoader();
            InputStream file = classLoader.getResourceAsStream("/file/turndown.js");         
            if(file == null){
                return "no resource found!";
            }
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            engine.eval(new InputStreamReader(file));
            engine.eval("x = TurndownService.turndown('<h1>Hello world!</h1>'));");
            ScriptContext newContext = new SimpleScriptContext();
            Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
            return (String) engineScope.get("x");
        } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : e.getStackTrace()) {
                sb.append(element.toString());
                sb.append("\n");
            }
            return e.toString() + " " + sb.toString();
        }        
    }
}