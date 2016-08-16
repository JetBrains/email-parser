package quoteParser


import quoteParser.features.*

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class QuoteParser(val lines: List<String>, sufficientFeatureCount: Int = 2) {
    // For single line headers
    private val featureSet: Set<AbstractQuoteFeature>
    private val sufficientFeatureCount: Int
    private val maxFeatureCount: Int
    private val foundFeatureMap: Map<String, Int> = mapOf()
    // ------

    // For multi lines and FWD headers
    private val MIDDLE_COLON_LINES_COUNT = 4
    private var middleColonCount = 0

    // TODO replace this array with offset
    private val middleColonLineIndex = IntArray(MIDDLE_COLON_LINES_COUNT) { -1 }
    // ------

    init {
        this.featureSet = setOf(
                DateFeature("DATE"),
                TimeFeature("TIME"),
                EmailFeature("EMAIL"),
                ColonFeature("COLON")
        )
        this.maxFeatureCount = this.featureSet.size
        if (sufficientFeatureCount < 1 || sufficientFeatureCount > this.maxFeatureCount) {
            throw IllegalArgumentException("sufficientFeatureCount must be in range 1..${this.maxFeatureCount}")
        }

        // TODO modify this.sufficientFeatureCount depend of In-Reply-To header.
        this.sufficientFeatureCount = sufficientFeatureCount
    }

    fun parse(): Content {
        // TODO
        return Content("".lines(), null, null)
    }
}