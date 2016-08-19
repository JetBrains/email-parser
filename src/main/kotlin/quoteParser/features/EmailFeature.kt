package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class EmailFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "EMAIL"
    override fun getRegex(): Regex {
        return Regex("(.*[\\s\\xA0])?\\S+@\\S+([\\s\\xA0].*)?")
    }

}