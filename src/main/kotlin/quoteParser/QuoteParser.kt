package quoteParser

import quoteParser.features.*

class QuoteParser(headerLinesCount: Int = 3,
                  multiLineHeaderLinesCount: Int = 6,
                  maxQuoteBlocksCount: Int = 3,
                  val deleteQuoteMarks: Boolean = true,
                  val isInReplyToEMLHeader: Boolean = false,
                  val recursive: Boolean = false) {

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
        if (!deleteQuoteMarks && recursive) {
            throw IllegalStateException("Can't perform recursive parsing without deleting '>'")
        }
        this.lines = listOf()
        this.quoteMarkFeature = QuoteMarkFeature()
        this.quoteHeaderLinesParser = QuoteHeaderLinesParser(
                headerLinesCount = headerLinesCount,
                multiLIneHeaderLinesCount = multiLineHeaderLinesCount
        )
        this.quoteMarkParser = QuoteMarkParser(
                maxQuoteBlocksCount = maxQuoteBlocksCount
        )
    }

    fun parse(lines: List<String>): Content {
        this.lines = lines

        val matchingLines = this.quoteMarkFeature.matchLines(this.lines)
        val headerLinesIndexes = this.quoteHeaderLinesParser.parse(this.lines, matchingLines)
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
                return Content(lines, null, null)
            headerLinesIndexes != null && quoteMarkIndex == null ->
                return this.createContent(
                        headerLinesIndexes.first,
                        headerLinesIndexes.second + 1,
                        matchingLines
                )
            headerLinesIndexes == null && quoteMarkIndex != null ->
                return this.createContent(
                        quoteMarkIndex,
                        matchingLines=matchingLines
                )
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
                return this.createContent(
                        startHeaderLinesIndex,
                        endHeaderLineIndex + 1,
                        matchingLines
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
            return this.createContent(
                    quoteMarkIndex,
                    matchingLines=matchingLines
            )
        } else {
            return createContent(startHeaderLinesIndex, endHeaderLineIndex + 1, matchingLines)
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

            return createContent(startHeaderLinesIndex, endHeaderLineIndex + 1, matchingLines)

        } else if (isQuotedTextBetween) {
            return this.createContent(
                    quoteMarkIndex,
                    matchingLines=matchingLines
            )
        }

        var startIndex = startHeaderLinesIndex
        if (matchingLines.subList(startHeaderLinesIndex, endHeaderLineIndex + 1).all { it.hasQuoteMark() }) {
            while (startIndex > 0 && matchingLines[startIndex - 1].hasQuoteMark()) {
                --startIndex
            }
        }

        return createContent(startIndex, endHeaderLineIndex + 1, matchingLines)
    }


    private fun createContent(fromIndex: Int,
                              toIndex: Int = fromIndex,
                              matchingLines: List<QuoteMarkMatchingResult>): Content {
        deleteQuoteMarks(fromIndex, toIndex, matchingLines)

        return if (!recursive) {
            Content(
                    this.lines.subList(0, fromIndex),
                    QuoteHeader(fromIndex, toIndex, this.lines.subList(fromIndex, toIndex)),
                    Content(
                            this.lines.subList(toIndex, this.lines.lastIndex + 1),
                            null,
                            null
                    )
            )
        } else {
            Content(
                    this.lines.subList(0, fromIndex),
                    QuoteHeader(fromIndex, toIndex, this.lines.subList(fromIndex, toIndex)),
                    this.parse(this.lines.subList(toIndex, this.lines.lastIndex + 1))
            )
        }
    }

    private fun deleteQuoteMarks(fromIndex: Int,
                                 toIndex: Int = fromIndex,
                                 matchingLines: List<QuoteMarkMatchingResult>) {
        if (this.deleteQuoteMarks &&
                (fromIndex == toIndex ||
                        checkQuoteMarkSuggestion(toIndex, this.lines, matchingLines))) {

            this.lines = this.lines.mapIndexed { i, s ->
                val line = s.trimStart()
                if (i >= fromIndex) {
                    when {
                        line.startsWith("> ") -> line.drop(2)
                        line.startsWith('>') -> line.drop(1)
                        else -> s
                    }
                } else {
                    s
                }
            }
        }
    }

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