package quoteParser

import quoteParser.features.QuoteMarkMatchingResult
import quoteParser.features.QuoteMarkFeature

/**
 * Created by Pavel.Zhuk on 25.08.2016.
 */
class QuoteMarkParser() {

    fun parse(lines: List<String>,
              matchesLines: List<QuoteMarkMatchingResult> = QuoteMarkFeature()
                      .matchLines(lines)): Int? {

        val startQuoteBlockIndex: Int
        var endQuoteBlockIndex: Int = matchesLines.size - 1

        while (endQuoteBlockIndex > -1 &&
                !matchesLines[endQuoteBlockIndex].hasQuoteMark()) {
            endQuoteBlockIndex--
        }

        if (endQuoteBlockIndex == -1) {
            return null
        } else {
            var matchesQuoteMarkIndex = endQuoteBlockIndex
            var lineIndex = endQuoteBlockIndex
            while (lineIndex > -1) {
                if (matchesLines[lineIndex].hasQuoteMark()) {
                    matchesQuoteMarkIndex = lineIndex
                }
                if (matchesLines[lineIndex] == QuoteMarkMatchingResult.NON_EMPTY) {
                    break
                }
                lineIndex--
            }
            startQuoteBlockIndex = matchesQuoteMarkIndex
        }
        // TODO delete that after adding IN-REPLY-TO header processing. One-line quotes are exists.
        return if (endQuoteBlockIndex - startQuoteBlockIndex == 0)
            null
        else
            startQuoteBlockIndex
    }
}