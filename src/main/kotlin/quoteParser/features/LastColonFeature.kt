package quoteParser.features

import org.intellij.lang.annotations.Language

class LastColonFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "LAST_COLON"
    override fun getRegex(): Regex {
        // Full regex for testing needs
        @Language("RegExp")
        val regex = "(.*[\\s\\p{C}\\p{Z}>])?.*:([\\p{C}\\p{Z}\\s]*)?"

        // Regex for matching colon(:) in the end of the line.
        return Regex("${this.startWhitespaceOptional}.*:(${this.whitespace}*)?")
    }
}
