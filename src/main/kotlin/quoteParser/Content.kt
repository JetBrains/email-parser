package quoteParser

import quoteParser.features.QuoteMarkMatchingResult
import quoteParser.features.checkQuoteMarkSuggestion

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
data class Content(val body: List<String>,
                   val header: QuoteHeader?,
                   val quote: Content?) {

    fun toString(addMarks: Boolean = false, uppercaseHeader: Boolean = false): String {
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
        val quoteText = this.quote?.body
                ?.joinToString(prefix = prefix, separator = separator, postfix = "")
                ?: ""

        return StringBuilder(bodyText)
                .append(headerText)
                .append(quoteText)
                .toString()
    }

    override fun toString(): String {
        return toString(addMarks = false)
    }

    companion object {
        /**
         * Create a Content structure with just a body.
         */
        fun create(lines: List<String>) = Content(lines, null, null)

        fun create(lines: List<String>, fromIndex: Int, toIndex: Int = fromIndex): Content {


            return Content(
                    lines.subList(0, fromIndex),
                    QuoteHeader(fromIndex, toIndex, lines.subList(fromIndex, toIndex)),
                    Content(
                            lines.subList(toIndex, lines.lastIndex + 1),
                            null,
                            null
                    )
            )
        }

        /**
         * Create a Content structure with leading '>' removed for header and quote.
         */
        fun create(lines: List<String>, matchedLinesQuoteMark: List<QuoteMarkMatchingResult>,
                   fromIndex: Int, toIndex: Int = fromIndex,
                   deleteQuoteMarks: Boolean = true): Content {

            if (deleteQuoteMarks &&
                    (fromIndex == toIndex ||
                            checkQuoteMarkSuggestion(toIndex, lines, matchedLinesQuoteMark))) {
                return create(
                        lines = lines.mapIndexed { i, s ->
                            val line = s.trimStart()
                            if (i >= fromIndex && line.startsWith('>')) {
                                line.drop(1)
                            } else {
                                s
                            }
                        },
                        fromIndex = fromIndex,
                        toIndex = toIndex
                )
            }

            return create(lines, fromIndex, toIndex)
        }
    }
}