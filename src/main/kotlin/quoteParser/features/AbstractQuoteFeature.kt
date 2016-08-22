package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
abstract class AbstractQuoteFeature() {
    abstract val name: String
    abstract protected fun getRegex(): Regex
    open fun matches(line: String): Boolean = getRegex().matches(line)
}