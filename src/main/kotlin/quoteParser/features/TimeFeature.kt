package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class TimeFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "TIME"
    override fun getRegex() =
            Regex("(.*[\\s\\xA0])?(([01]?[0-9]|2[0-3]):([0-5][0-9])(:[0-5][0-9])?[,\\.]?:?)([\\s\\xA0].*)?")
}