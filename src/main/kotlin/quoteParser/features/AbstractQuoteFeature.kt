package quoteParser.features

import org.intellij.lang.annotations.Language

abstract class AbstractQuoteFeature() {
    @Language("Regexp")
    protected val whitespace = "[\\p{C}\\p{Z}\\s]"
    @Language("Regexp")
    protected val startWhitespaceOptional = "(.*[\\s\\p{C}\\p{Z}>])?"
    @Language("Regexp")
    protected val endWhitespaceOptional = "([\\p{C}\\p{Z}\\s].*)?"
    @Language("Regexp")
    protected val startBracketsOptional = "\\p{C}*[\\.,\\{\\[<\\*\\(:\"'`\\|\\\\/~]?\\p{C}*"
    @Language("Regexp")
    protected val endBracketsOptional = "\\p{C}*[\\.,}\\]>\\*\\):\"'`\\|\\\\/~;]?\\p{C}*"

    abstract val name: String

    abstract protected fun getRegex(): Regex

    open fun matches(line: String): Boolean = this.getRegex().matches(line)
}