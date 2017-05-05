/*
 * Copyright 2016-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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