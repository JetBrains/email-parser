package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class ColonFeature(name: String) : AbstractQuoteFeature(name) {
    override fun getRegex(): Regex {
        return Regex("(.*\\s)?.*:(\\s*)?")
    }
}
