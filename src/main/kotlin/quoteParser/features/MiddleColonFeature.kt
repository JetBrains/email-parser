package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class MiddleColonFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "MIDDLE_COLON"
    override fun getRegex(): Regex {
        // This regex matches the colon after any word except the last one with optional
        // whitespace before the colon and obligatory whitespace after the colon.
        return Regex(".*\\S+${this.whitespace}*:${this.whitespace}+\\S+.*")
    }
}
