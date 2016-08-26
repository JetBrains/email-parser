package quoteParser

import quoteParser.features.*

// TODO Do smth with false-positive logs and stack traces (not urgent)
class QuoteParser(SUFFICIENT_FEATURE_COUNT: Int = 2,
                  HEADER_LINES_COUNT: Int = 3,
                  MULTI_LINE_HEADER_LINES_COUNT: Int = 6,
                  MAX_QUOTE_BLOCKS_COUNT: Int = 3) {

    enum class Relation() {
        HEADER_LINES_FIRST,
        QUOTE_MARK_FIRST,
        QUOTE_MARK_IN_HEADER_LINES
    }

    private var lines: List<String>
    private val quoteMarkFeature: QuoteMarkFeature
    private val quoteHeaderLinesParser: QuoteHeaderLinesParser
    private val quoteMarkParser: QuoteMarkParser

    init {
        this.lines = listOf()
        this.quoteMarkFeature = QuoteMarkFeature()
        this.quoteHeaderLinesParser = QuoteHeaderLinesParser(
                SUFFICIENT_FEATURE_COUNT,
                HEADER_LINES_COUNT,
                MULTI_LINE_HEADER_LINES_COUNT
        )
        this.quoteMarkParser = QuoteMarkParser(MAX_QUOTE_BLOCKS_COUNT)
    }

    fun parse(lines: List<String>): Content {
        this.lines = lines

        val matchingLines = this.quoteMarkFeature.matchLines(this.lines)
        val headerLinesIndexes = this.quoteHeaderLinesParser.parse(this.lines)
        val quoteMarkIndex = this.quoteMarkParser.parse(this.lines, matchingLines)

        when {
            headerLinesIndexes == null && quoteMarkIndex == null ->
                return Content.create(this.lines)
            headerLinesIndexes != null && quoteMarkIndex == null ->
                return Content.create(
                        this.lines,
                        headerLinesIndexes.first,
                        headerLinesIndexes.second + 1
                )
            headerLinesIndexes == null && quoteMarkIndex != null ->
                return Content.create(this.lines, quoteMarkIndex)
        }

        // Without this condition smart cast doesn't work.
        if (headerLinesIndexes == null || quoteMarkIndex == null)
            throw IllegalStateException("Never gets there")

        val startHeaderLinesIndex = headerLinesIndexes.first
        val endHeaderLineIndex = headerLinesIndexes.second

        val relation = this.getRelation(
                startHeaderLinesIndex,
                endHeaderLineIndex,
                quoteMarkIndex
        )

        when (relation) {
            Relation.QUOTE_MARK_IN_HEADER_LINES -> {
                return this.getContentSameLinesCase(
                        startHeaderLinesIndex,
                        endHeaderLineIndex
                )
            }
            Relation.QUOTE_MARK_FIRST -> {
                return this.getContentQuoteMarkFirstCase(
                        startHeaderLinesIndex,
                        endHeaderLineIndex,
                        quoteMarkIndex,
                        matchingLines
                )
            }
            Relation.HEADER_LINES_FIRST -> {
                return this.getContentHeaderLinesFirstCase(
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

        val isTextBetween = this.isTextBetween(endHeaderLineIndex, quoteMarkIndex, matchingLines)
        val isQuoteMarksAroundHeaderLines = this.isQuoteMarksAroundHeaderLines(
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
            return Content.create(this.lines, quoteMarkIndex)
        } else {
            return Content.create(this.lines, startHeaderLinesIndex, endHeaderLineIndex + 1)
        }
    }

    private fun getContentQuoteMarkFirstCase(startHeaderLinesIndex: Int, endHeaderLineIndex: Int, quoteMarkIndex: Int,
                                             matchingLines: List<QuoteMarkMatchingResult>): Content {

        val isTextBetween = this.isTextBetween(quoteMarkIndex, startHeaderLinesIndex, matchingLines)
        val isQuotedTextBetween = this.isQuotedTextBetween(quoteMarkIndex, startHeaderLinesIndex, matchingLines)

        if (isTextBetween) {

            var i = startHeaderLinesIndex
            while (i > 0 && matchingLines[i - 1] != QuoteMarkMatchingResult.NON_EMPTY)
                --i
            if (i == 0 || matchingLines.subList(i, this.lines.size).any() { it.hasQuoteMark() }) {
                val msg = "Relation = ${Relation.QUOTE_MARK_FIRST}. Found quoteMark(>), but in the lines after ${i - 1} must not be any quote mark!"
                throw IllegalStateException(msg)
            }

            return Content.create(this.lines, startHeaderLinesIndex, endHeaderLineIndex + 1)

        } else if (isQuotedTextBetween) {
            return Content.create(this.lines, quoteMarkIndex)
        }

        var startIndex = startHeaderLinesIndex
        if (matchingLines.subList(startHeaderLinesIndex, endHeaderLineIndex + 1).all { it.hasQuoteMark() }) {
            while (startIndex > 0 && matchingLines[startIndex - 1].hasQuoteMark()) {
                --startIndex
            }
        }

        return Content.create(this.lines, startIndex, endHeaderLineIndex + 1)
    }

    private fun getContentSameLinesCase(startHeaderLinesIndex: Int, endHeaderLineIndex: Int) =
            Content.create(this.lines, startHeaderLinesIndex, endHeaderLineIndex + 1)


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