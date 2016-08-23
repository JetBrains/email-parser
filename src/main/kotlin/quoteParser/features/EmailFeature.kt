package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class EmailFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "EMAIL"
    override fun getRegex(): Regex {
        return Regex("(.*[\\p{C}\\p{Z}\\s])?\\S+@\\S+([\\s\\p{C}\\p{Z}].*)?")
    }

}