package quoteParser

import quoteParser.features.*

// TODO Do smth with false-positive logs and stack traces (not urgent)
class QuoteParser(val lines: List<String>) {
    enum class Relation() {
        HEADER_LINES_FIRST,
        QUOTE_MARK_FIRST,
        QUOTE_MARK_IN_HEADER_LINES
    }

    fun parse(): Content {
        val matchingLines = QuoteMarkFeature().matchLines(lines)
        val headerLinesIndexes = QuoteHeaderLinesParser(lines).parse()
        val quoteMarkIndex = QuoteMarkParser().parse(lines, matchingLines)

        when {
            headerLinesIndexes == null && quoteMarkIndex == null ->
                return Content.create(lines)
            headerLinesIndexes != null && quoteMarkIndex == null ->
                return Content.create(
                        lines,
                        headerLinesIndexes.first,
                        headerLinesIndexes.second + 1
                )
            headerLinesIndexes == null && quoteMarkIndex != null ->
                return Content.create(lines, quoteMarkIndex)
        }

        // Without this condition smart cast doesn't work.
        if (headerLinesIndexes == null || quoteMarkIndex == null)
            throw IllegalStateException("Never gets there")

        val startHeaderLinesIndex = headerLinesIndexes.first
        val endHeaderLineIndex = headerLinesIndexes.second

        val relation = getRelation(
                startHeaderLinesIndex,
                endHeaderLineIndex,
                quoteMarkIndex
        )

        when (relation) {
            Relation.QUOTE_MARK_IN_HEADER_LINES -> {
                return getContentSameLinesCase(
                        startHeaderLinesIndex,
                        endHeaderLineIndex
                )
            }
            Relation.QUOTE_MARK_FIRST -> {
                return getContentQuoteMarkFirstCase(
                        startHeaderLinesIndex,
                        endHeaderLineIndex,
                        quoteMarkIndex,
                        matchingLines
                )
            }
            Relation.HEADER_LINES_FIRST -> {
                return getContentHeaderLinesFirstCase(
                        startHeaderLinesIndex,
                        endHeaderLineIndex,
                        quoteMarkIndex,
                        matchingLines
                )
            }
        }
    }

    private fun getContentHeaderLinesFirstCase(startHeaderLinesIndex: Int, endHeaderLineIndex: Int, quoteMarkIndex: Int,
                                               matchingLines: List<QuoteMarkMatchingResult>): Content {

        val isTextBetween = isTextBetween(endHeaderLineIndex, quoteMarkIndex, matchingLines)
        val isQuoteMarksAroundHeaderLines = isQuoteMarksAroundHeaderLines(
                startHeaderLinesIndex,
                endHeaderLineIndex,
                quoteMarkIndex,
                matchingLines
        )

        if (!isTextBetween && !isQuoteMarksAroundHeaderLines) {
            val msg = "Relation = ${Relation.HEADER_LINES_FIRST}. Both isTextBetween and isHeaderLinesHasQuoteMark " +
                    "are false, but it can't be!"
            throw IllegalStateException(msg)
        }

        if (isTextBetween && isQuoteMarksAroundHeaderLines) {
            return Content.create(lines, quoteMarkIndex)
        } else {
            return Content.create(lines, startHeaderLinesIndex, endHeaderLineIndex + 1)
        }
    }

    private fun getContentQuoteMarkFirstCase(startHeaderLinesIndex: Int, endHeaderLineIndex: Int, quoteMarkIndex: Int,
                                             matchingLines: List<QuoteMarkMatchingResult>): Content {

        val isTextBetween = isTextBetween(quoteMarkIndex, startHeaderLinesIndex, matchingLines)
        val isQuotedTextBetween = isQuotedTextBetween(quoteMarkIndex, startHeaderLinesIndex, matchingLines)

        if (isTextBetween) {

            var i = startHeaderLinesIndex
            while (i > 0 && matchingLines[i - 1] != QuoteMarkMatchingResult.NON_EMPTY)
                --i
            if (i == 0 || matchingLines.subList(i, lines.size).any() { it.hasQuoteMark() }) {
                val msg = "Relation = ${Relation.QUOTE_MARK_FIRST}. Found quoteMark(>), but in the lines after ${i - 1} must not be any quote mark!"
                throw IllegalStateException(msg)
            }

            return Content.create(lines, startHeaderLinesIndex, endHeaderLineIndex + 1)

        } else if (isQuotedTextBetween) {
            return Content.create(lines, quoteMarkIndex)
        }

        var startIndex = startHeaderLinesIndex
        if (matchingLines.subList(startHeaderLinesIndex, endHeaderLineIndex + 1).all { it.hasQuoteMark() }) {
            while (startIndex > 0 && matchingLines[startIndex - 1].hasQuoteMark()) {
                --startIndex
            }
        }

        return Content.create(lines, startIndex, endHeaderLineIndex + 1)
    }

    private fun getContentSameLinesCase(startHeaderLinesIndex: Int, endHeaderLineIndex: Int) =
            Content.create(lines, startHeaderLinesIndex, endHeaderLineIndex + 1)


    private fun getRelation(startHeaderLinesIndex: Int, endHeaderLinesIndex: Int,
                            quoteMarkIndex: Int) =
            when {
                quoteMarkIndex < startHeaderLinesIndex ->
                    Relation.QUOTE_MARK_FIRST
                quoteMarkIndex > endHeaderLinesIndex ->
                    Relation.HEADER_LINES_FIRST
                else ->
                    Relation.QUOTE_MARK_IN_HEADER_LINES
            }

    private fun isTextBetween(startIndex: Int, endIndex: Int,
                              matchingLines: List<QuoteMarkMatchingResult>) =
            (startIndex + 1..endIndex - 1).any() {
                matchingLines[it] == QuoteMarkMatchingResult.NON_EMPTY
            }

    private fun isQuotedTextBetween(startIndex: Int, endIndex: Int,
                                    matchingLines: List<QuoteMarkMatchingResult>) =
            (startIndex + 1..endIndex - 1).any() {
                matchingLines[it] == QuoteMarkMatchingResult.V_NON_EMPTY
            }

    private fun isQuoteMarksAroundHeaderLines(startHeaderLinesIndex: Int,
                                              endHeaderLinesIndex: Int,
                                              quoteMarkIndex: Int,
                                              matchingLines: List<QuoteMarkMatchingResult>): Boolean {
        val headerLinesContainsQuoteMarks = (startHeaderLinesIndex..endHeaderLinesIndex).all() {
            matchingLines[it].hasQuoteMark()
        }
        if (headerLinesContainsQuoteMarks) {
            return true
        }
        for (i in endHeaderLinesIndex + 1..quoteMarkIndex) {
            if (matchingLines[i].hasQuoteMark()) {
                return true
            }
            if (matchingLines[i] == QuoteMarkMatchingResult.NON_EMPTY) {
                return false
            }
        }
        return true
    }
}