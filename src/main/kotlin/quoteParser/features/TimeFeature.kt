package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class TimeFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "TIME"
    override fun getRegex(): Regex  {
        @Language("RegExp")
        val hhmm = "([01]?[0-9]|2[0-3])\\p{C}*:\\p{C}*([0-5][0-9])"

        @Language("RegExp")
        val sec = "\\p{C}*:\\p{C}*[0-5][0-9]"

        @Language("RegExp")
        val ampm = "\\p{C}*[aApP]\\p{C}*[,\\.]?\\p{C}*[mM]\\p{C}*[,\\.]?"

        val time = "${startSpaceOptional}($startBracketsOptional($hhmm)($sec)?($ampm)?$endBracketsOptional)${endSpaceOptional}"

//      Full date regexp for testing needs.
//        @Language("RegExp")
//        val regex = "(.*[\\s\\p{C}\\p{Z}])?(\\p{C}*[\\.,\\{\\[<\\*\\(:\"'`\\|\\\\/~]?\\p{C}*(([01]?[0-9]|2[0-3])\\p{C}*:\\p{C}*([0-5][0-9]))(\\p{C}*:\\p{C}*[0-5][0-9])?(\\p{C}*[aApP]\\p{C}*[,\\.]?\\p{C}*[mM]\\p{C}*[,\\.]?)?\\p{C}*[\\.,}\\]>\\*\\):\"'`\\|\\\\/~;]?\\p{C}*)([\\s\\p{C}\\p{Z}].*)?"


        return Regex(time)
    }
}
