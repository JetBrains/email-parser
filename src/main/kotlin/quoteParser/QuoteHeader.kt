package quoteParser

/**
 * Represent header of the quote.
 * 
 * @property startIndex number of the string of the email message where header of the quote begins (inclusive)
 * @property endIndex number of the string of the email message where header of the quote ends (exclusive)
 * @property text header lines
 */
data class QuoteHeader(val startIndex: Int,
                       val endIndex: Int = startIndex,
                       val text: List<String>) {

    override fun toString(): String {
        return this.text.joinToString(separator = "\n")
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other == null -> false
            other !is QuoteHeader -> false
            this.startIndex != other.startIndex -> false
            this.endIndex != other.endIndex -> false
            this.text != other.text -> false
            else -> true
        }
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + startIndex
        result = prime * result + endIndex
        result = prime * result + text.hashCode()
        return result
    }
}