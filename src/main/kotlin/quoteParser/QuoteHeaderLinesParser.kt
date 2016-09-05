package quoteParser

import quoteParser.features.*

/**
 * Created by Pavel.Zhuk on 25.08.2016.
 */
class QuoteHeaderLinesParser(val headerLinesCount: Int = 3,
                             val multiLIneHeaderLinesCount: Int = 6) {

    // For single line headers
    private val featureSet: Array<AbstractQuoteFeature>
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
        this.featureSet = arrayOf(
                DateFeature(),
                TimeFeature(),
                EmailFeature(),
                LastColonFeature()
        )
        this.maxFeatureCount = this.featureSet.size

        this.sufficientFeatureCount = 2
    }

    private fun prepare() {
        this.foundFeatureMap.clear()

        this.middleColonCount = 0
        this.lineMatchesMiddleColon = false
        this.firstMiddleColonLineIndex = -1

        this.foundPhraseFeature = false
        this.phraseFeatureLineIndex = -1
    }

    fun parse(lines: List<String>,
              matchedLinesQuoteMark: List<QuoteMarkMatchingResult> =
              QuoteMarkFeature().matchLines(lines)): Pair<Int, Int>? {
        this.prepare()
        this.lines = lines

        this.lines.forEachIndexed { lineIndex, line ->

            this.resetSingleLineFeatures(oldLineIndex = lineIndex - this.headerLinesCount, all = false)

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
                this.resetFeatures(oldLineIndex = lineIndex - this.headerLinesCount)
            } else {
                this.resetFeatures(all = true)
            }

            if (this.headerFound(matchedLinesQuoteMark)) {
                return@parse this.identifyHeader()
            }
        }

        return null
    }

    private fun updatePhraseFeature(lineIndex: Int) {
        this.foundPhraseFeature = true
        this.phraseFeatureLineIndex = lineIndex
    }

    private fun headerFound(matchedLinesQuoteMark: List<QuoteMarkMatchingResult>) =
            when {
                this.foundPhraseFeature -> true
                this.foundFeatureMap.size > this.sufficientFeatureCount -> true
                this.foundFeatureMap.size == this.sufficientFeatureCount
                        && this.checkForSecondaryFeatures(matchedLinesQuoteMark) -> true
                else -> false
            }

    private fun checkForSecondaryFeatures(matchedLinesQuoteMark: List<QuoteMarkMatchingResult>): Boolean {
        val sortedIndexes = this.foundFeatureMap.values.sorted()
        val startIdx = sortedIndexes.first()
        val endIdx = sortedIndexes.last()

        return when {
            this.checkMiddleColonSuggestion(startIdx, endIdx) -> true
            checkQuoteMarkSuggestion(endIdx, lines, matchedLinesQuoteMark) -> true
            else -> false
        }
    }

    /**
     * If supposed header lines contains MiddleColonFeature most often
     * it is a multi line header.
     */
    private fun checkMiddleColonSuggestion(startIdx: Int, endIdx: Int): Boolean {
        return this.lines.subList(startIdx, endIdx + 1)
                .any { this.middleColonFeature.matches(it) }
    }


    private fun updateSingleLineFeature(lineIndex: Int, feature: AbstractQuoteFeature) {
        if (feature is LastColonFeature) {

            // LastColonFeature cannot be the first feature of the quote.
            if (this.foundFeatureMap.size == 0) {
                return
            }

            val sortedIndexes = this.foundFeatureMap.values.sorted()
            val startIdx = sortedIndexes.first()
            val endIdx = sortedIndexes.last()

            // LastColonFeature cannot be in multi line header.
            if (checkMiddleColonSuggestion(startIdx, endIdx)) {
                val headerLinesCount = endIdx - startIdx + 1

                // It seems like MiddleColonFeature instead LastColonFeature.
                if (this.middleColonCount <= headerLinesCount) {
                    updateMultiLineFeature(lineIndex)

                    // If line contains the real MiddleColonFeature
                    // then we should decrease its count.
                    if (this.middleColonFeature.matches(this.lines[lineIndex])) {
                        this.middleColonCount--
                    }
                }
                return
            }
        }
        this.foundFeatureMap[feature.name] = lineIndex
    }

    private fun updateMultiLineFeature(lineIndex: Int) {
        this.lineMatchesMiddleColon = true
        if (this.middleColonCount == this.multiLIneHeaderLinesCount) {
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
                toIndex - fromIndex + 1 < this.headerLinesCount) {  // Found suggestions are placed in less than HEADER_LINES_COUNT lines.

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
            var cnt = this.multiLIneHeaderLinesCount - this.middleColonCount
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