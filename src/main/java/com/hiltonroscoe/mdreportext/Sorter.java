package com.hiltonroscoe.mdreportext;
import java.util.*;
// import com.nomagic.magicreport.engine.Tool; 
/**
 * Hello world!
 *
 */
public class Sorter {
    public static ArrayList sortByLength(ArrayList collection) {
        collection.sort(new java.util.Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                // TODO: Argument validation (nullity, length)
                return s1.length() - s2.length();// comparision
            }
        });
        return collection;
    }
}
