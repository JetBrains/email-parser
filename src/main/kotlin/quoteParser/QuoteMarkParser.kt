package quoteParser

import quoteParser.features.QuoteMarkMatchingResult
import quoteParser.features.QuoteMarkFeature

// MAX_QUOTE_BLOCKS_COUNT = 1 is good for hiding fragmented quotes
// but a small portion of useful content is become hidden as well.
internal class QuoteMarkParser(val maxQuoteBlocksCount: Int = 3,
                      val minimumQuoteBlockSize: Int = 7) {

    internal fun parse(lines: List<String>,
              matchingLines: List<QuoteMarkMatchingResult> = QuoteMarkFeature().matchLines(lines)): Int? {

        val quoteBlocksCount = this.getQuoteBlocksCount(matchingLines)
        if (quoteBlocksCount > this.maxQuoteBlocksCount) {
            return null
        }

        val startQuotedBlockIndex: Int
        var endQuotedBlockIndex: Int = matchingLines.size - 1

        while (endQuotedBlockIndex > -1 &&
                !matchingLines[endQuotedBlockIndex].hasQuoteMark()) {
            endQuotedBlockIndex--
        }

        var quoteMarksCount = 0
        if (endQuotedBlockIndex == -1) {
            return null
        } else {
            var matchingQuoteMarkIndex = endQuotedBlockIndex
            var lineIndex = endQuotedBlockIndex
            while (lineIndex > -1) {
                if (matchingLines[lineIndex].hasQuoteMark()) {
                    quoteMarksCount++
                    matchingQuoteMarkIndex = lineIndex
                }
                if (matchingLines[lineIndex] == QuoteMarkMatchingResult.NOT_EMPTY) {
                    break
                }
                lineIndex--
            }
            startQuotedBlockIndex = matchingQuoteMarkIndex
        }

        return if (quoteMarksCount < this.minimumQuoteBlockSize)
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