package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class DateFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "DATE"
    override fun getRegex(): Regex{
        @Language("RegExp")
        val shordDateForward = "[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*(20)?[0-9]{2}"
        @Language("RegExp")
        val shortDateReversed = "(20)?[0-9]{2}\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]"
        val shortDate = "(($shordDateForward)|($shortDateReversed))\\p{C}*[,\\.]{0,2}:?" 
  
        @Language("RegExp")
        val fullDateForward = "([0-3]?[0-9]\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}(20\\d\\d\\p{C}*[\\.,]{0,2})"
        @Language("RegExp")
        val fullDateReversed = "(20\\d\\d\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}([0-3]?[0-9]\\p{C}*[\\.,]{0,2})"
        val fullDate = "($fullDateForward)|($fullDateReversed)" 
        
        val date = "(.*[\\p{C}\\p{Z}\\s:])?(($shortDate)|($fullDate))${endSpaceOptional}"

//      Full date regexp for testing needs.
//        @Language("RegExp")
//        val regex = "(.*[\\p{C}\\p{Z}\\s:])?(((([0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*(20)?[0-9]{2})|((20)?[0-9]{2}\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]))\\p{C}*[,\\.]{0,2}:?)|((([0-3]?[0-9]\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}(20\\d\\d\\p{C}*[\\.,]{0,2}))|((20\\d\\d\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}([0-3]?[0-9]\\p{C}*[\\.,]{0,2}))))([\\p{C}\\p{Z}\\s].*)?"
        
        return Regex(date)
    }
            
}
