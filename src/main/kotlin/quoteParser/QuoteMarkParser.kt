package quoteParser

import quoteParser.features.QuoteMarkMatchingResult
import quoteParser.features.QuoteMarkFeature

/**
 * Created by Pavel.Zhuk on 25.08.2016.
 */
class QuoteMarkParser() {

    fun parse(lines: List<String>,
              matchingLines: List<QuoteMarkMatchingResult> = QuoteMarkFeature()
                      .matchLines(lines)): Int? {

        val startQuotedBlockIndex: Int
        var endQuotedBlockIndex: Int = matchingLines.size - 1

        while (endQuotedBlockIndex > -1 &&
                !matchingLines[endQuotedBlockIndex].hasQuoteMark()) {
            endQuotedBlockIndex--
        }

        if (endQuotedBlockIndex == -1) {
            return null
        } else {
            var matchingQuoteMarkIndex = endQuotedBlockIndex
            var lineIndex = endQuotedBlockIndex
            while (lineIndex > -1) {
                if (matchingLines[lineIndex].hasQuoteMark()) {
                    matchingQuoteMarkIndex = lineIndex
                }
                if (matchingLines[lineIndex] == QuoteMarkMatchingResult.NON_EMPTY) {
                    break
                }
                lineIndex--
            }
            startQuotedBlockIndex = matchingQuoteMarkIndex
        }
        // TODO delete that after adding IN-REPLY-TO header processing. One-line quotes are exists.
        return if (endQuotedBlockIndex - startQuotedBlockIndex == 0)
            null
        else
            startQuotedBlockIndex
    }
}