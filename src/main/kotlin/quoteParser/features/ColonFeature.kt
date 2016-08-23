package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class ColonFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "COLON"
    override fun getRegex(): Regex {
        // Regex for matching colon(:) in the end of the line.
        return Regex("${startWhitespaceOptional}.*:([\\p{C}\\p{Z}\\s]*)?")
    }
}
