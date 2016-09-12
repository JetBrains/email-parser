package quoteParser

/**
 * Email message with separate body, header of the quote
 * and citation itself.
 * 
 * @property body useful content of the message
 * @property header header of the quote if exists
 * @property quote the rest part of the message if exists
 */
data class Content(val body: List<String>,
                   val header: QuoteHeader?,
                   val quote: Content?) {

    /**
     * Get string representation of Content object.
     * 
     * @param addMarks if set to true adds '>' from the start of the header of the quote till the end of the message
     * @param uppercaseHeader if set to true then converts header to uppercase 
     * @return string representation of the object
     */
    fun toString(addMarks: Boolean = true, uppercaseHeader: Boolean = false): String {
        val prefix = if (addMarks) "> " else ""
        val separator = if (addMarks) "\n> " else "\n"

        val bodyText = if (!this.body.isEmpty()) {
            this.body.joinToString(
                    separator = "\n",
                    postfix = if (this.header != null) "\n" else ""
            )
        } else {
            ""
        }

        val headerText = if (this.header != null && !this.header.text.isEmpty())
            this.header.text
                    .joinToString(prefix = prefix, separator = separator, postfix = "\n") {
                        if (uppercaseHeader) it.toUpperCase() else it
                    }
        else
            ""

        val quoteText = if (this.quote != null && !this.quote.body.isEmpty())
                this.quote
                        .toString(addMarks, uppercaseHeader).lines()
                        .joinToString(prefix = prefix, separator = separator, postfix = "")
        else
            ""

        return StringBuilder(bodyText)
                .append(headerText)
                .append(quoteText)
                .toString()
    }

    override fun toString(): String {
        return toString(addMarks = true, uppercaseHeader = false)
    }
}