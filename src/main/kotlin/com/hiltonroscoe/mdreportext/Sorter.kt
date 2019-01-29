package com.hiltonroscoe.mdreportext

import com.nomagic.magicreport.engine.Tool
import org.apache.commons.beanutils.PropertyUtils
import java.util.*

/**
 * Sorts classes by their names
 * Used by older version of the Glossary.
 */
class Sorter : Tool() {
  companion object {
    private val serialVersionUID = 1L
    /**
     * Sorts classes by their name, in decending order.
     * @param collection Collection of classes
     * @return A sorted collection of classes
     */
    @JvmStatic
    fun sortByLength(collection: ArrayList<*>): ArrayList<*> {
        Collections.sort(collection, Comparator { o1, o2 ->
        try
        {
          val s1 = PropertyUtils.getProperty(o1, "name") as String
          val s2 = PropertyUtils.getProperty(o2, "name") as String
            return@Comparator s2.length - s1.length// comparision
        }
        catch (e:Exception) {
           return@Comparator 0
        }
      })
      return collection
    }
  }
}