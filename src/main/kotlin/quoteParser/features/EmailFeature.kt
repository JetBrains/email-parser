package quoteParser.features

import org.intellij.lang.annotations.Language

class EmailFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "EMAIL"

    override fun getRegex(): Regex {
        // Full regex for testing needs
        @Language("RegExp")
        val regex = "(.*[\\s\\p{C}\\p{Z}>])?\\S+@\\S+([\\p{C}\\p{Z}\\s].*)?"

        // @ symbol surrounded with at least one non-whitespace symbol.
        return Regex("${this.startWhitespaceOptional}\\S+@\\S+${this.endWhitespaceOptional}")
    }

}