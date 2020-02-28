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
<<<<<<< Updated upstream
        catch (e:Exception) {
           return@Comparator 0
=======

        /** Exposes Collections.reverse method */
        @JvmStatic
        fun reverse(list: List<*>) {
            return Collections.reverse(list)
        }

        @JvmStatic
                /**
                 * Sorts attributes by their name, with special rules for NIST CDFs.
                 * @param collection Collection of attributes
                 * @return A sorted collection of attributes
                 */
        fun sortByRule(collection: ArrayList<*>): ArrayList<*> {
            Collections.sort(collection, Comparator { o1, o2 ->
                try {
                    val normalizeString = { sInput: String ->
                        if (sInput.startsWith("Other"))
                            sInput.substringAfter("Other").toLowerCase() + " "
                        else if (Regex("^End[A-Z]*").containsMatchIn(sInput))
                            sInput.substringAfter("End").toLowerCase() + " "
                        else if (Regex("^Start[A-Z]*").containsMatchIn(sInput))
                            sInput.substringAfter("Start").toLowerCase()
                        else if (sInput.endsWith("End"))
                            sInput.substringBefore("End").toLowerCase() + "start "
                        else
                            sInput.toLowerCase()

                    }
                    val s1 = PropertyUtils.getProperty(o1, "name") as String
                    val s2 = PropertyUtils.getProperty(o2, "name") as String
                    return@Comparator normalizeString(s1).compareTo(normalizeString(s2))// comparision
                } catch (e: Exception) {
                    System.err.println(e.toString())
                    return@Comparator 0
                }
            })
            return collection
>>>>>>> Stashed changes
        }
      })
      return collection
    }
  }
}