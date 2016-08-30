package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class DateFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "DATE"

    override fun getRegex(): Regex {
        // Short date format starts with day number (e.g. 15-02-2016)
        @Language("RegExp")
        val shortDateForward = "[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*(20)?[0-9]{2}"
        // Short date format starts with year (2016-02-15)
        @Language("RegExp")
        val shortDateReversed = "(20)?[0-9]{2}\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]"
        val shortDate = "(($shortDateForward)|($shortDateReversed))\\p{C}*[,\\.]{0,2}"

        // Full date format with up to three possible arbitrary words 
        // between number of the day and year. Number of the day goes first
        // (e.g. 15 Feb 2016; 15 Thu, Feb 2016, 15 x y z 2016).
        val fullDateForward = "([0-3]?[0-9]\\p{C}*[\\.,]{0,2}${this.whitespace}+)" +
                "(\\S+${this.whitespace}+){0,3}(20\\d\\d\\p{C}*[\\.,]{0,2})"

        // The same as previous but year and number of the day are swapped.
        val fullDateReversed = "(20\\d\\d\\p{C}*[\\.,]{0,2}${this.whitespace}+)" +
                "(\\S+${this.whitespace}+){0,3}([0-3]?[0-9]\\p{C}*[\\.,]{0,2})"
        val fullDate = "($fullDateForward)|($fullDateReversed)"

        // Final date regex. Colon(:) is possible before the date, 
        // arbitrary bracket is possible after the date.
        val date = "(.*[\\p{C}\\p{Z}\\s:>])?(($shortDate)|($fullDate))${this.endBracketsOptional}" +
                "${this.endWhitespaceOptional}"

        // Full date regex for testing needs.
        @Language("RegExp")
        val regex = "(.*[\\p{C}\\p{Z}\\s:>])?(((([0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*(20)?[0-9]{2})|((20)?[0-9]{2}\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]\\p{C}*[/.-]\\p{C}*[0-3]?[0-9]))\\p{C}*[,\\.]{0,2})|((([0-3]?[0-9]\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}(20\\d\\d\\p{C}*[\\.,]{0,2}))|((20\\d\\d\\p{C}*[\\.,]{0,2}[\\p{C}\\p{Z}\\s]+)(\\S+[\\p{C}\\p{Z}\\s]+){0,3}([0-3]?[0-9]\\p{C}*[\\.,]{0,2}))))\\p{C}*[\\.,}\\]>\\*\\):\"'`\\|\\\\/~;]?\\p{C}*([\\p{C}\\p{Z}\\s].*)?"

        return Regex(date)
    }

}
