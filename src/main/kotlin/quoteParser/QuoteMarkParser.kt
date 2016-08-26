package quoteParser

import quoteParser.features.QuoteMarkMatchingResult
import quoteParser.features.QuoteMarkFeature

/**
 * Created by Pavel.Zhuk on 25.08.2016.
 */
// MAX_QUOTE_BLOCKS_COUNT = 1 is good for hiding piecewise quotes
// but a small portion of useful content is become hidden as well.
class QuoteMarkParser(private val MAX_QUOTE_BLOCKS_COUNT: Int = 3) {

    fun parse(lines: List<String>,
              matchingLines: List<QuoteMarkMatchingResult> = QuoteMarkFeature()
                      .matchLines(lines)): Int? {

        val quoteBlocksCount = this.getQuoteBlocksCount(matchingLines)
        if (quoteBlocksCount > this.MAX_QUOTE_BLOCKS_COUNT) {
            return null
        }

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

    private fun getQuoteBlocksCount(matchingLines: List<QuoteMarkMatchingResult>): Int {

        val from = matchingLines.indexOfFirst { it.hasQuoteMark() }
        val to = matchingLines.indexOfLast { it.hasQuoteMark() }

        if (from == -1 || to == -1) {
            return 0
        }

        var quoteBlocksCount = 0
        for (i in from + 1..to) {
            if (matchingLines[i - 1].hasQuoteMark() &&
                    matchingLines[i].isTextWithoutQuoteMark()) {
                quoteBlocksCount++
            }
        }
        return quoteBlocksCount + 1
    }
}