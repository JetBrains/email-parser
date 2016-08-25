package quoteParser

import quoteParser.features.*

/**
 * Created by Pavel.Zhuk on 25.08.2016.
 */
class QuoteHeaderLinesParser(val lines: List<String>, sufficientFeatureCount: Int = 2,
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


    init {
        this.featureSet = setOf(
                DateFeature(),
                TimeFeature(),
                EmailFeature(),
                ColonFeature()
        )
        this.maxFeatureCount = this.featureSet.size
        if (sufficientFeatureCount < 1 || sufficientFeatureCount > this.maxFeatureCount) {
            throw IllegalArgumentException("sufficientFeatureCount must be in range 1..${this.maxFeatureCount}")
        }

        // TODO modify this.sufficientFeatureCount depend of In-Reply-To header.
        this.sufficientFeatureCount = sufficientFeatureCount
    }

    private fun prepare() {
        foundFeatureMap.clear()

        middleColonCount = 0
        lineMatchesMiddleColon = false
        firstMiddleColonLineIndex = -1

        foundPhraseFeature = false
        phraseFeatureLineIndex = -1
    }

    fun parse(): Pair<Int, Int>? {
        prepare()

        lines.forEachIndexed { lineIndex, line ->
            var anyFeatureMatches = false

            featureSet.forEach { feature ->
                if (feature.matches(line)) {
                    updateSingleLineFeature(lineIndex, feature)
                    anyFeatureMatches = true
                }
            }

            if (middleColonFeature.matches(line)) {
                updateMultiLineFeature(lineIndex)
                anyFeatureMatches = true
            }

            if (phraseFeature.matches(line)) {
                updatePhraseFeature(lineIndex)
            }

            if (anyFeatureMatches) {
                resetFeatures(oldLineIndex = lineIndex - HEADER_LINES_COUNT)
            } else {
                resetFeatures(all = true)
            }

            if (headerFound()) {
                return@parse identifyHeader()
            }
        }

        return null
    }

    private fun updatePhraseFeature(lineIndex: Int) {
        foundPhraseFeature = true
        phraseFeatureLineIndex = lineIndex
    }

    private fun headerFound() =
            foundFeatureMap.size >= sufficientFeatureCount || foundPhraseFeature

    private fun updateSingleLineFeature(lineIndex: Int, feature: AbstractQuoteFeature) {
        foundFeatureMap[feature.name] = lineIndex
    }

    private fun updateMultiLineFeature(lineIndex: Int) {
        lineMatchesMiddleColon = true
        if (middleColonCount == MULTI_LINE_HEADER_LINES_COUNT) {
            firstMiddleColonLineIndex++
        } else {
            if (middleColonCount == 0) {
                firstMiddleColonLineIndex = lineIndex
            }
            middleColonCount++
        }
    }

    private fun resetFeatures(oldLineIndex: Int = -1, all: Boolean = false) {
        resetSingleLineFeatures(oldLineIndex, all)
        resetMultiLineFeatures()
    }

    private fun resetMultiLineFeatures(shouldReset: Boolean = true) {
        if (!lineMatchesMiddleColon && shouldReset) {
            middleColonCount = 0

        }
        lineMatchesMiddleColon = false
    }

    private fun resetSingleLineFeatures(oldLineIndex: Int, all: Boolean) {
        if (all) {
            foundFeatureMap.clear()
        } else {
            val temp: MutableMap<String, Int> = mutableMapOf()
            foundFeatureMap.filterTo(temp) {
                it.value > oldLineIndex
            }
            foundFeatureMap = temp
        }
    }

    private fun identifyHeader(): Pair<Int, Int>? {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        if (foundPhraseFeature) {
            fromIndex = phraseFeatureLineIndex
            toIndex = phraseFeatureLineIndex
        } else {
            foundFeatureMap.forEach {
                fromIndex = Math.min(fromIndex, it.value)
                toIndex = Math.max(toIndex, it.value)
            }

            while (checkForAllRemainingFeatures(fromIndex, toIndex)) {
                toIndex++
            }

            if (checkForMultiLineHeader(fromIndex, toIndex)) {
                fromIndex = firstMiddleColonLineIndex
                toIndex = firstMiddleColonLineIndex + middleColonCount - 1
            }

//            if (matchedLines.subList(fromIndex, toIndex + 1).all { it }) {
//                while (fromIndex > 0 && matchedLines[fromIndex - 1]) {
//                    --fromIndex
//                }
//            }
        }

        // TODO add some tricky removal of angle brackets (not urgent)
        return Pair(fromIndex, toIndex + 1)
    }

    // If sufficient count of features had been found in less then HEADER_LINES_COUNT
    // lines try to check others not found features in the following line.
    private fun checkForAllRemainingFeatures(fromIndex: Int, toIndex: Int): Boolean {
        val remainingFeatures = featureSet.filter { !foundFeatureMap.containsKey(it.name) }
        if (remainingFeatures.isNotEmpty() && // One suggestion is not found.
                toIndex < lines.size - 1 && // There is the following line to check.
                toIndex - fromIndex + 1 < HEADER_LINES_COUNT) {  // Found suggestions are placed in less than HEADER_LINES_COUNT lines.

            val lineIndex = toIndex + 1

            var anyFeatureMatches = false
            remainingFeatures.forEach { feature ->
                if (feature.matches(lines[lineIndex])) {
                    updateSingleLineFeature(lineIndex, feature)
                    anyFeatureMatches = true
                }
            }

            if (anyFeatureMatches) {

                if (middleColonFeature.matches(lines[lineIndex])) {
                    updateMultiLineFeature(lineIndex)
                }
                resetMultiLineFeatures()

                return@checkForAllRemainingFeatures true
            }
        }
        return false
    }

    // Check if there is a multi line header or FWD
    private fun checkForMultiLineHeader(fromIndex: Int, toIndex: Int): Boolean {
        if (middleColonCount >= toIndex - fromIndex + 1) {
            var cnt = MULTI_LINE_HEADER_LINES_COUNT - middleColonCount
            var lineIndex = toIndex + 1
            while (cnt > 0 && lineIndex < lines.size) {

                if (middleColonFeature.matches(lines[lineIndex])) {
                    updateMultiLineFeature(lineIndex)
                } else {
                    break
                }

                lineIndex++
                cnt--
            }

            return firstMiddleColonLineIndex <= fromIndex &&
                    firstMiddleColonLineIndex + middleColonCount - 1 >= toIndex
        }
        return false
    }
}