package com.hiltonroscoe.mdreportext;

import java.util.*;
import com.nomagic.magicreport.engine.Tool;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Hello world!
 *
 */
public class Sorter extends Tool {
    public static List sortByLength(List collection) {

        collection.sort(new java.util.Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    String s1 = (String) PropertyUtils.getProperty(o1, "name");
                    String s2 = (String) PropertyUtils.getProperty(o2, "name");
                    return s2.length() - s1.length();// comparision
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        return collection;
    }
}
