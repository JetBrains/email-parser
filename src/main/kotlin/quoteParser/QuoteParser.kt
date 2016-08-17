package quoteParser

import quoteParser.features.*

// TODO Do smth with false-positive logs and stack traces (not urgent)
class QuoteParser(val lines: List<String>, sufficientFeatureCount: Int = 2) {
    // For single line headers
    private val featureSet: Set<AbstractQuoteFeature>
    private val sufficientFeatureCount: Int
    private val maxFeatureCount: Int
    private val foundFeatureMap: MutableMap<String, Int> = mutableMapOf()
    // ------

    // For multi lines and FWD headers
    // TODO simplify this
    // TODO what if to increase MCLC?? Will it work better or worse? Should experiment with that (not urgent)
    private val MIDDLE_COLON_LINES_COUNT = 4
    private var middleColonCount = 0
    private val middleColonFeature = MiddleColonFeature("MIDDLE_COLON")

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
        foundFeatureMap.clear()
        middleColonCount = 0

        lines.forEachIndexed { lineIndex, line ->
            // TODO should not replace suggestion lineIndex if it is repeats. Only if it is too old.
            var anyFeatureMatches = false
            for (feature in featureSet) {
                if (feature.matches(line)) {
                    foundFeatureMap[feature.name] = lineIndex
                    anyFeatureMatches = true
                }
            }
            if (matchMiddleColon(lineIndex, line)) {
                anyFeatureMatches = true
            }

            if (anyFeatureMatches) {
                resetSuggestions(lineIndex = lineIndex)
            } else {
                resetSuggestions(all = true)
            }

            if (foundFeatureMap.size >= sufficientFeatureCount) {
                return@parse getHeader(lines)
            }
        }
        return Content(lines, null, null)
    }

    // TODO didn't find middle_colon in email 40
    // TODO Sometimes last colon is special case of middle colon
    // TODO sparse multi line headers
    private fun matchMiddleColon(lineIndex: Int, line: String, shouldReset: Boolean = true): Boolean {
        if (middleColonFeature.matches(line) &&
                (middleColonCount == 0 || lineIndex - 1 == middleColonLineIndex[middleColonCount - 1])) {
            if (middleColonCount == MIDDLE_COLON_LINES_COUNT) {
                middleColonLineIndex.forEachIndexed { i, line ->
                    if (i < middleColonCount - 1)
                        middleColonLineIndex[i] = middleColonLineIndex[i + 1]
                }
                middleColonLineIndex[middleColonCount - 1] = lineIndex
            } else {
                middleColonLineIndex[middleColonCount++] = lineIndex
            }
            return true
        } else {
            if (shouldReset) {
                middleColonCount = 0
            }
            return false
        }
    }

    private fun resetSuggestions(lineIndex: Int = 0, all: Boolean = false) {
        if (all) {
            foundFeatureMap.clear()
        } else {
            foundFeatureMap.filterTo(foundFeatureMap, {
                lineIndex - it.value <= 2
            })
        }
    }

    private fun getHeader(lines: List<String>): Content {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        foundFeatureMap.forEach {
            fromIndex = Math.min(fromIndex, it.value)
            toIndex = Math.max(toIndex, it.value)
        }

        var additionalLine = 0

        // TODO Check all of the remaining suggestions, not the first one.
        // If sufficient count of suggestions had been found in one or two lines
        // try to check first not found suggestion in the following line.
        val feature = featureSet.firstOrNull { !foundFeatureMap.containsKey(it.name) }
        if (feature != null && // One suggestion is not found.
                toIndex < lines.size - 1 && // There is the following line to check.
                toIndex - fromIndex < 2) {  // Found suggestions are placed in less than 3 lines.

            val lineIndex = toIndex + 1

            matchMiddleColon(lineIndex, lines[lineIndex])

            if (feature.matches(lines[lineIndex])) {
                foundFeatureMap[feature.name] = lineIndex
                toIndex++
            } else {
                additionalLine = 1
            }
        }

        // TODO check previous lines for middle colons
        // Try to check if there is a multi line header or FWD
        if (middleColonCount >= toIndex - fromIndex + 1) {
            var cnt = MIDDLE_COLON_LINES_COUNT - middleColonCount
            var lineIndex = toIndex + 1 + additionalLine
            while (cnt > 0 && lineIndex < lines.size) {

                val updated = matchMiddleColon(lineIndex, lines[lineIndex], shouldReset = false)
                if (!updated) {
                    break
                }

                lineIndex++
                cnt--
            }

            if (middleColonLineIndex[0] <= fromIndex && middleColonLineIndex[middleColonCount - 1] >= toIndex) {
                fromIndex = middleColonLineIndex[0]
                toIndex = middleColonLineIndex[middleColonCount - 1]
            }
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
}