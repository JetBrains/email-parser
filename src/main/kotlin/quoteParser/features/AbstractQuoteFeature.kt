package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
abstract class AbstractQuoteFeature() {
    @Language("Regexp")
    protected val startSpaceOptional = "(.*[\\s\\p{C}\\p{Z}])?"
    @Language("Regexp")
    protected val endSpaceOptional = "([\\p{C}\\p{Z}\\s].*)?"
    @Language("Regexp")
    protected val startBracketsOptional = "\\p{C}*[\\.,\\{\\[<\\*\\(:\"'`\\|\\\\/~]?\\p{C}*"
    @Language("Regexp")
    protected val endBracketsOptional = "\\p{C}*[\\.,}\\]>\\*\\):\"'`\\|\\\\/~;]?\\p{C}*"
    abstract val name: String
    abstract protected fun getRegex(): Regex
    open fun matches(line: String): Boolean = getRegex().matches(line)
}