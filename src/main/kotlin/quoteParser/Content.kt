package quoteParser

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
data class Content(val body: List<String>, val header: QuoteHeader? ,val quote: Content?) {

    override fun toString(): String {
        return StringBuilder(body.joinToString(separator = "\n", postfix = "\n"))
                .append(header?.text?.joinToString(prefix = "> ", separator = "\n> ", postfix = "\n") ?: "")
                .append(quote?.body?.joinToString(prefix = "> ", separator = "\n> ", postfix = "\n") ?: "")
                .toString()
    }
}