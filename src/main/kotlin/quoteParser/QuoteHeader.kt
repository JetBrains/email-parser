package quoteParser

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
data class QuoteHeader(val startIndex: Int,
                       val endIndex: Int = startIndex,
                       val text: List<String>) {

    override fun toString(): String {
        return text.joinToString(separator = "\n")
    }
}