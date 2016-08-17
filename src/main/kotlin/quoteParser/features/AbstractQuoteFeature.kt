package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
// TODO remove useless constructor
abstract class AbstractQuoteFeature(val name: String) {
    abstract protected fun getRegex(): Regex
    fun matches(line: String): Boolean = getRegex().matches(line)
}