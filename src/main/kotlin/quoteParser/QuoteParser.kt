package quoteParser

import quoteParser.features.*

// TODO Do smth with false-positive logs and stack traces (not urgent)
class QuoteParser(val lines: List<String>, sufficientFeatureCount: Int = 2) {
    // For single line headers
    private val featureSet: Set<AbstractQuoteFeature>
    private val sufficientFeatureCount: Int
    private val maxFeatureCount: Int
    private var foundFeatureMap: MutableMap<String, Int> = mutableMapOf()
    // ------

    // For multi line and FWD headers
    // TODO !!!what if to increase MCLC?? Will it work better or worse? Should experiment with that (not urgent)
    private val MIDDLE_COLON_LINES_COUNT = 4
    private var middleColonCount = 0
    private val middleColonFeature = MiddleColonFeature()
    private var lineMatchesMiddleColon = false
    private var firstMiddleColonLineIndex = -1
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
    }

    fun parse(): Content {
        prepare()

        lines.forEachIndexed { lineIndex, line ->
            // TODO should not replace suggestion lineIndex if it is repeats. Only if it is too old.
            var anyFeatureMatches = false

            for (feature in featureSet) {
                if (feature.matches(line)) {
                    updateSingleLineFeature(lineIndex, feature)
                    anyFeatureMatches = true
                }
            }

            if (middleColonFeature.matches(line)) {
                updateMultiLineFeature(lineIndex)
                anyFeatureMatches = true
            }

            // TODO !!!old line idx
            if (anyFeatureMatches) {
                resetFeatures(oldLineIndex = lineIndex - 3)
            } else {
                resetFeatures(all = true)
            }

            if (foundFeatureMap.size >= sufficientFeatureCount) {
                return@parse identifyHeader()
            }
        }
        return Content(lines, null, null)
    }

    private fun updateSingleLineFeature(lineIndex: Int, feature: AbstractQuoteFeature) {
        foundFeatureMap[feature.name] = lineIndex
    }

    // TODO !!!didn't find middle_colon in email 40
    // TODO Sometimes last colon is special case of middle colon
    // TODO sparse multi line headers
    private fun updateMultiLineFeature(lineIndex: Int) {
        lineMatchesMiddleColon = true
        if (middleColonCount == MIDDLE_COLON_LINES_COUNT) {
            firstMiddleColonLineIndex++
        } else  {
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

    private fun resetMultiLineFeatures() {
        if (!lineMatchesMiddleColon) {
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

    private fun identifyHeader(): Content {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        foundFeatureMap.forEach {
            fromIndex = Math.min(fromIndex, it.value)
            toIndex = Math.max(toIndex, it.value)
        }

        if (checkForRemainingFeature(fromIndex, toIndex)) {
            toIndex++
        }

        if (checkForMultiLineHeader(fromIndex, toIndex)) {
            fromIndex = firstMiddleColonLineIndex
            toIndex = firstMiddleColonLineIndex + middleColonCount - 1
        }

        // TODO add some tricky removal of angle brackets (not urgent)
        return Content(
                lines.subList(0, fromIndex),
                QuoteHeader(fromIndex, toIndex, lines.subList(fromIndex, toIndex + 1)),
                Content(
                        lines.subList(toIndex + 1, lines.lastIndex + 1),
                        null,
                        null
                )
        )
    }

    // TODO !!!Check all of the remaining suggestions, not the first one.
    // If sufficient count of suggestions had been found in one or two lines
    // try to check first not found suggestion in the following line (-es??).
    /**
     * @return
     * -1 if there is no remaining features left.
     *
     *  0 if feature doesn't match the following line.
     *
     *  1 if feature matches the following line.
     */
    private fun checkForRemainingFeature(fromIndex: Int, toIndex: Int): Boolean {
        val feature = featureSet.firstOrNull { !foundFeatureMap.containsKey(it.name) }
        if (feature != null && // One suggestion is not found.
                toIndex < lines.size - 1 && // There is the following line to check.
                toIndex - fromIndex < 2) {  // Found suggestions are placed in less than 3 lines.

            val lineIndex = toIndex + 1

            if (feature.matches(lines[lineIndex])) {

                // useless for now
//                updateSingleLineFeature(lineIndex, feature)

                if (middleColonFeature.matches(lines[lineIndex])) {
                    updateMultiLineFeature(lineIndex)
                }
                resetMultiLineFeatures()

                return true
            }
        }
        return false
    }

    // TODO check previous lines for middle colons ????
    // Try to check if there is a multi line header or FWD
    private fun checkForMultiLineHeader(fromIndex: Int, toIndex: Int): Boolean {
        if (middleColonCount >= toIndex - fromIndex + 1) {
            var cnt = MIDDLE_COLON_LINES_COUNT - middleColonCount
            var lineIndex = toIndex + 1
            while (cnt > 0 && lineIndex < lines.size) {

                if (middleColonFeature.matches(lines[lineIndex])) {
                    updateMultiLineFeature(lineIndex)
                    lineMatchesMiddleColon = false
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