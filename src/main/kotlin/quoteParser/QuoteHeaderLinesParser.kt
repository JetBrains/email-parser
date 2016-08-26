package quoteParser

import quoteParser.features.*

/**
 * Created by Pavel.Zhuk on 25.08.2016.
 */
class QuoteHeaderLinesParser(SUFFICIENT_FEATURE_COUNT: Int = 2,
                             private val HEADER_LINES_COUNT: Int = 3,
                             private val MULTI_LINE_HEADER_LINES_COUNT: Int = 6) {

    // For single line headers
    private val featureSet: Set<AbstractQuoteFeature>
    private val sufficientFeatureCount: Int
    private val maxFeatureCount: Int
    private var foundFeatureMap: MutableMap<String, Int> = mutableMapOf()
    // ------

    // For multi line and FWD headers
    private var middleColonCount = 0
    private val middleColonFeature = MiddleColonFeature()
    private var lineMatchesMiddleColon = false
    private var firstMiddleColonLineIndex = -1
    // ------

    // For phraseFeature
    private var foundPhraseFeature = false
    private var phraseFeatureLineIndex = -1
    private val phraseFeature = PhraseFeature()
    // ------

    private var lines: List<String> = listOf()

    init {
        this.featureSet = setOf(
                DateFeature(),
                TimeFeature(),
                EmailFeature(),
                ColonFeature()
        )
        this.maxFeatureCount = this.featureSet.size
        if (SUFFICIENT_FEATURE_COUNT < 1 || SUFFICIENT_FEATURE_COUNT > this.maxFeatureCount) {
            throw IllegalArgumentException("sufficientFeatureCount must be in range 1..${this.maxFeatureCount}")
        }

        // TODO modify this.sufficientFeatureCount depend of In-Reply-To header.
        this.sufficientFeatureCount = SUFFICIENT_FEATURE_COUNT
    }

    private fun prepare() {
        this.foundFeatureMap.clear()

        this.middleColonCount = 0
        this.lineMatchesMiddleColon = false
        this.firstMiddleColonLineIndex = -1

        this.foundPhraseFeature = false
        this.phraseFeatureLineIndex = -1
    }

    fun parse(lines: List<String>): Pair<Int, Int>? {
        this.prepare()
        this.lines = lines

        this.lines.forEachIndexed { lineIndex, line ->
            var anyFeatureMatches = false

            this.featureSet.forEach { feature ->
                if (feature.matches(line)) {
                    this.updateSingleLineFeature(lineIndex, feature)
                    anyFeatureMatches = true
                }
            }

            if (this.middleColonFeature.matches(line)) {
                this.updateMultiLineFeature(lineIndex)
                anyFeatureMatches = true
            }

            if (this.phraseFeature.matches(line)) {
                this.updatePhraseFeature(lineIndex)
            }

            if (anyFeatureMatches) {
                this.resetFeatures(oldLineIndex = lineIndex - HEADER_LINES_COUNT)
            } else {
                this.resetFeatures(all = true)
            }

            if (this.headerFound()) {
                return@parse this.identifyHeader()
            }
        }

        return null
    }

    private fun updatePhraseFeature(lineIndex: Int) {
        this.foundPhraseFeature = true
        this.phraseFeatureLineIndex = lineIndex
    }

    private fun headerFound() =
            this.foundFeatureMap.size >= this.sufficientFeatureCount || this.foundPhraseFeature

    private fun updateSingleLineFeature(lineIndex: Int, feature: AbstractQuoteFeature) {
        this.foundFeatureMap[feature.name] = lineIndex
    }

    private fun updateMultiLineFeature(lineIndex: Int) {
        this.lineMatchesMiddleColon = true
        if (this.middleColonCount == this.MULTI_LINE_HEADER_LINES_COUNT) {
            this.firstMiddleColonLineIndex++
        } else {
            if (this.middleColonCount == 0) {
                this.firstMiddleColonLineIndex = lineIndex
            }
            this.middleColonCount++
        }
    }

    private fun resetFeatures(oldLineIndex: Int = -1, all: Boolean = false) {
        this.resetSingleLineFeatures(oldLineIndex, all)
        this.resetMultiLineFeatures()
    }

    private fun resetMultiLineFeatures(shouldReset: Boolean = true) {
        if (!this.lineMatchesMiddleColon && shouldReset) {
            this.middleColonCount = 0

        }
        this.lineMatchesMiddleColon = false
    }

    private fun resetSingleLineFeatures(oldLineIndex: Int, all: Boolean) {
        if (all) {
            this.foundFeatureMap.clear()
        } else {
            val temp: MutableMap<String, Int> = mutableMapOf()
            this.foundFeatureMap.filterTo(temp) {
                it.value > oldLineIndex
            }
            this.foundFeatureMap = temp
        }
    }

    private fun identifyHeader(): Pair<Int, Int>? {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        if (this.foundPhraseFeature) {
            fromIndex = this.phraseFeatureLineIndex
            toIndex = this.phraseFeatureLineIndex
        } else {
            this.foundFeatureMap.forEach {
                fromIndex = Math.min(fromIndex, it.value)
                toIndex = Math.max(toIndex, it.value)
            }

            while (this.checkForAllRemainingFeatures(fromIndex, toIndex)) {
                toIndex++
            }

            if (this.checkForMultiLineHeader(fromIndex, toIndex)) {
                fromIndex = this.firstMiddleColonLineIndex
                toIndex = this.firstMiddleColonLineIndex + this.middleColonCount - 1
            }
        }

        // TODO add some tricky removal of angle brackets (not urgent)
        return Pair(fromIndex, toIndex)
    }

    // If sufficient count of features had been found in less then HEADER_LINES_COUNT
    // lines try to check others not found features in the following line.
    private fun checkForAllRemainingFeatures(fromIndex: Int, toIndex: Int): Boolean {
        val remainingFeatures = this.featureSet.filter { !this.foundFeatureMap.containsKey(it.name) }
        if (remainingFeatures.isNotEmpty() && // One suggestion is not found.
                toIndex < this.lines.size - 1 && // There is the following line to check.
                toIndex - fromIndex + 1 < this.HEADER_LINES_COUNT) {  // Found suggestions are placed in less than HEADER_LINES_COUNT lines.

            val lineIndex = toIndex + 1

            var anyFeatureMatches = false
            remainingFeatures.forEach { feature ->
                if (feature.matches(this.lines[lineIndex])) {
                    this.updateSingleLineFeature(lineIndex, feature)
                    anyFeatureMatches = true
                }
            }

            if (anyFeatureMatches) {

                if (this.middleColonFeature.matches(this.lines[lineIndex])) {
                    this.updateMultiLineFeature(lineIndex)
                }
                this.resetMultiLineFeatures()

                return@checkForAllRemainingFeatures true
            }
        }
        return false
    }

    // Check if there is a multi line header or FWD
    private fun checkForMultiLineHeader(fromIndex: Int, toIndex: Int): Boolean {
        if (this.middleColonCount >= toIndex - fromIndex + 1) {
            var cnt = this.MULTI_LINE_HEADER_LINES_COUNT - this.middleColonCount
            var lineIndex = toIndex + 1
            while (cnt > 0 && lineIndex < this.lines.size) {

                if (this.middleColonFeature.matches(this.lines[lineIndex])) {
                    this.updateMultiLineFeature(lineIndex)
                } else {
                    break
                }

                lineIndex++
                cnt--
            }

            return this.firstMiddleColonLineIndex <= fromIndex &&
                    this.firstMiddleColonLineIndex + this.middleColonCount - 1 >= toIndex
        }
        return false
    }
}