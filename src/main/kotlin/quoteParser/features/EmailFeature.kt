package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class EmailFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "EMAIL"

    override fun getRegex(): Regex {
        // Full regex for testing needs
        @Language("RegExp")
        val regex = "(.*[\\s\\p{C}\\p{Z}>])?\\S+@\\S+(${this.whitespace}.*)?"

        // @ symbol surrounded with at least one non-whitespace symbol.
        return Regex("${this.startWhitespaceOptional}\\S+@\\S+${this.endWhitespaceOptional}")
    }

}