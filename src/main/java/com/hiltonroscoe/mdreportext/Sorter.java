package com.hiltonroscoe.mdreportext;

import java.util.*;
import com.nomagic.magicreport.engine.Tool;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Sorts classes by their names
 * Used by older version of the Glossary.
 */
public class Sorter extends Tool {
    private static final long serialVersionUID = 1L;
/**
 * Sorts classes by their name, in decending order. 
 * @param collection Collection of classes
 * @return A sorted collection of classes
 */
    public static List sortByLength(List collection) {

        collection.sort((o1, o2) -> {
            try {
                String s1 = (String) PropertyUtils.getProperty(o1, "name");
                String s2 = (String) PropertyUtils.getProperty(o2, "name");
                return s2.length() - s1.length();// comparision
            } catch (Exception e) {
                return 0;
            }
        });
        return collection;
    }
}
