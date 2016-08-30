package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class LastColonFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "LAST_COLON"
    override fun getRegex(): Regex {
        // Full regex for testing needs
        @Language("RegExp")
        val regex = "(.*[\\s\\p{C}\\p{Z}>])?.*:(${this.whitespace}*)?"

        // Regex for matching colon(:) in the end of the line.
        return Regex("${this.startWhitespaceOptional}.*:(${this.whitespace}*)?")
    }
}
