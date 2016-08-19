package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class MiddleColonFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "MIDDLE_COLON"
    override fun getRegex(): Regex {
        return Regex(".*\\S+\\s*:\\s+\\S+.*")
    }

}
