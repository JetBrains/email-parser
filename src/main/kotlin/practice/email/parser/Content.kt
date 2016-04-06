package practice.email.parser

/**
 * Data class for storing email body, quote and signature.
 */
data class Content(val body: String, val quote: Content?, val signature: String?) {
    
    override fun toString(): String {
        return StringBuilder(body)
                .append(quote ?: "")
                .append(signature ?: "")
                .toString()
    }
}
