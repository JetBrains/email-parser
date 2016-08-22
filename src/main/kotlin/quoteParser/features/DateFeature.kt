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
        val shordDateForward = "[0-3]?[0-9][/.-][0-3]?[0-9][/.-](20)?[0-9]{2}"
        @Language("RegExp")
        val shortDateReversed = "(20)?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]"
        val shortDate = listOf<String>(
                shordDateForward,
                shortDateReversed
        ).joinToString(prefix = "((", separator = ")|(", postfix = "))[,\\.]{0,2}:?")

        @Language("RegExp")
        val fullDateForward = "([0-3]?[0-9][\\.,]{0,2}[\\s\\xA0]+)(\\S+[\\s\\xA0]+){0,2}(20\\d\\d[\\.,]{0,2})"
        @Language("RegExp")
        val fullDateReversed = "(20\\d\\d[\\.,]{0,2}[\\s\\xA0]+)(\\S+[\\s\\xA0]+){0,2}([0-3]?[0-9][\\.,]{0,2})"
        val fullDate = listOf<String>(
                fullDateForward,
                fullDateReversed
        ).joinToString(prefix = "(", separator = ")|(", postfix = ")")
        
        val date = listOf<String>(
                shortDate,
                fullDate
        ).joinToString(prefix = "(.*[\\s\\xA0:])?((", separator = ")|(", postfix = "))([\\s\\xA0].*)?")

        return Regex(date)
    }
            
}
