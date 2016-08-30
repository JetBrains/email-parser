package quoteParser

import quoteParser.features.*

// TODO Do smth with false-positive logs and stack traces (not urgent)
class QuoteParser(headerLinesCount: Int = 3,
                  multiLineHeaderLinesCount: Int = 6,
                  maxQuoteBlocksCount: Int = 3,
                  val isInReplyToEMLHeader: Boolean = false) {

    private enum class Relation() {
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
                headerLinesCount = headerLinesCount,
                multiLIneHeaderLinesCount = multiLineHeaderLinesCount,
                isInReplyToEMLHeader = this.isInReplyToEMLHeader
        )
        this.quoteMarkParser = QuoteMarkParser(
                maxQuoteBlocksCount = maxQuoteBlocksCount
        )
    }

    fun parse(lines: List<String>): Content {
        this.lines = lines

        val matchingLines = this.quoteMarkFeature.matchLines(this.lines)
        val headerLinesIndexes = this.quoteHeaderLinesParser.parse(this.lines)
        val quoteMarkIndex =
                // This condition means: for EMLs without In-Reply-To header search for
                // quotation marks(>) only if quoteHeaderLines is null. It works well for
                // test data, but it is weird.. because it skips quotation marks most of
                // the time.
                // As alternative this condition may be deleted, but it works
                // worse with some cases...
                if (!this.isInReplyToEMLHeader && headerLinesIndexes != null) {
                    null
                } else {
                    this.quoteMarkParser.parse(this.lines, matchingLines)
                }
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
            throw IllegalStateException("Never gets here")

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
            while (i > 0 && matchingLines[i - 1] != QuoteMarkMatchingResult.NOT_EMPTY)
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
                matchingLines[it] == QuoteMarkMatchingResult.NOT_EMPTY
            }

    private fun isQuotedTextBetween(startIndex: Int, endIndex: Int,
                                    matchingLines: List<QuoteMarkMatchingResult>) =
            (startIndex + 1..endIndex - 1).any() {
                matchingLines[it] == QuoteMarkMatchingResult.V_NOT_EMPTY
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
            if (matchingLines[i] == QuoteMarkMatchingResult.NOT_EMPTY) {
                return false
            }
        }
        return true
    }
}