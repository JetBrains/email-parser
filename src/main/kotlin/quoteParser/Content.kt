package quoteParser

import quoteParser.features.QuoteMarkMatchingResult
import quoteParser.features.checkQuoteMarkSuggestion

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
data class Content(val body: List<String>,
                   val header: QuoteHeader?,
                   val quote: Content?) {

    fun toString(addMarks: Boolean = true, uppercaseHeader: Boolean = false): String {
        val prefix = if (addMarks) "> " else ""
        val separator = if (addMarks) "\n> " else "\n"

        val bodyText = this.body.joinToString(
                separator = "\n",
                postfix = if (this.header != null) "\n" else ""
        )
        val headerText = if (this.header != null && !this.header.text.isEmpty())
            this.header.text
                    .joinToString(prefix = prefix, separator = separator, postfix = "\n") {
                        if (uppercaseHeader) it.toUpperCase() else it
                    }
        else
            ""
        val quoteText = this.quote?.
                toString(addMarks, uppercaseHeader)?.
                lines()?.joinToString(prefix = prefix, separator = separator, postfix = "")
                ?: ""

        return StringBuilder(bodyText)
                .append(headerText)
                .append(quoteText)
                .toString()
    }

    override fun toString(): String {
        return toString(addMarks = false)
    }
}