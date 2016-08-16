package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class TimeFeature(name: String) : AbstractQuoteFeature(name) {
    override fun getRegex() =
            Regex("(.*\\s)?(([01]?[0-9]|2[0-3]):([0-5][0-9])(:[0-5][0-9])?[,\\.]?:?)(\\s.*)?")
}