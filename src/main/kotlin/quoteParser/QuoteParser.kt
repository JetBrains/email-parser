package quoteParser

import quoteParser.features.*
import java.io.File
import javax.mail.internet.MimeMessage

private enum class Relation() {
    HEADER_LINES_FIRST,
    QUOTE_MARK_FIRST,
    QUOTE_MARK_IN_HEADER_LINES
}

private fun getRelation(startHeaderLinesIndex: Int, endHeaderLinesIndex: Int, quoteMarkIndex: Int) =
        when {
            quoteMarkIndex < startHeaderLinesIndex ->
                Relation.QUOTE_MARK_FIRST
            quoteMarkIndex > endHeaderLinesIndex ->
                Relation.HEADER_LINES_FIRST
            else ->
                Relation.QUOTE_MARK_IN_HEADER_LINES
        }

private fun isTextBetween(startIndex: Int, endIndex: Int, matchingLines: List<QuoteMarkMatchingResult>) =
        (startIndex + 1..endIndex - 1).any() {
            matchingLines[it] == QuoteMarkMatchingResult.NOT_EMPTY
        }

private fun isQuotedTextBetween(startIndex: Int, endIndex: Int, matchingLines: List<QuoteMarkMatchingResult>) =
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

/**
 * A parser to separate a quote from useful email content.
 * 
 * To instantiate this class use [QuoteParser.Builder].      
 * Typical usage: create **QuoteParser** instance with [builder][QuoteParser.Builder]
 * and call its [parse()][QuoteParser.parse] method either with file or list of strings.
 * 
 * For now it works only with *text/plain* MIME type. 
 */
class QuoteParser private constructor(builder: Builder) {

    private val deleteQuoteMarks: Boolean
    private val recursive: Boolean

    private val quoteMarkFeature: QuoteMarkFeature
    private val quoteHeaderLinesParser: QuoteHeaderLinesParser
    private val quoteMarkParser: QuoteMarkParser

    private var lines: List<String> = listOf()

    /**
     * Builder for [QuoteParser] class.
     * 
     * All customizable parameters have the default value.
     * In most cases you do not need to modify them.
     */
    class Builder {
        internal var headerLinesCount: Int = 3
        internal var multiLineHeaderLinesCount: Int = 6
        internal var maxQuoteBlocksCount: Int = 3
        internal var minimumQuoteBlockSize: Int = 7
        internal var deleteQuoteMarks: Boolean = true
        internal var recursive: Boolean = false
        internal var keyPhrases: List<String> = KeyPhrases.default

        /**
         * Maximum number of lines going one after another to
         * identify key features of the quote. Default = 3.
         */
        fun headerLinesCount(value: Int): Builder {
            this.headerLinesCount = value
            return this
        }
        
        /**
         * Maximum number of lines going one after another to
         * check [MiddleColonFeature]. Used in multiline 
         * headers of the quote. Default = 6.
         */
        fun multiLineHeaderLinesCount(value: Int): Builder {
            this.multiLineHeaderLinesCount = value
            return this
        }

        /**
         * Maximum number of blocks marked with '>' symbol. 
         * If this number is exceeded then all quote marks (>)
         * are ignored. Default = 3.
         */
        fun maxQuoteBlocksCount(value: Int): Builder {
            this.maxQuoteBlocksCount = value
            return this
        }

        /**
         * Minimum number of lines going one after another with mark '>' to
         * identify quote block. If the real number is below than specified
         * then in most cases it is not a real quote. Default = 7.
         */
        fun minimumQuoteBlockSize(value: Int): Builder {
            this.minimumQuoteBlockSize = value
            return this
        }

        /**
         * Specify whether quote marks (>) should be deleted from the original
         * email. Default = true.
         */
        fun deleteQuoteMarks(value: Boolean): Builder {
            this.deleteQuoteMarks = value
            return this
        }

        /**
         * Specify whether to analyze the nested citations.
         * If set true then [deleteQuoteMarks] must also be true. 
         * It is impossible to parse nested citations without deleting '>'.
         * Default = false.
         */
        fun recursive(value: Boolean): Builder {
            this.recursive = value
            return this
        }
        
        /**
         *  User can specify list of regexes defining a quote beginning.
         *  If any of the regex from the list is matched then quote is defined. 
         *  Default = [KeyPhrases.default]
         *  @param value list of the user defined regexes
         */
        fun keyPhrases(value: List<String>): Builder {
            this.keyPhrases = value
            return this
        }

        fun build(): QuoteParser {
            return QuoteParser(this)
        }

        fun build(init: Builder.() -> Unit): QuoteParser {
            val builder = Builder()
            builder.init()
            return QuoteParser(builder)
        }
    }

    init {
        this.deleteQuoteMarks = builder.deleteQuoteMarks
        this.recursive = builder.recursive

        if (!this.deleteQuoteMarks && this.recursive) {
            throw IllegalStateException("Can't perform recursive parsing without deleting '>'")
        }

        this.quoteMarkFeature = QuoteMarkFeature()
        this.quoteHeaderLinesParser = QuoteHeaderLinesParser(
                headerLinesCount = builder.headerLinesCount,
                multiLIneHeaderLinesCount = builder.multiLineHeaderLinesCount,
                keyPhrases = builder.keyPhrases
        )
        this.quoteMarkParser = QuoteMarkParser(
                maxQuoteBlocksCount = builder.maxQuoteBlocksCount,
                minimumQuoteBlockSize = builder.minimumQuoteBlockSize
        )
    }

    /**
     * Parse the given file to separate a quote from the useful content.   
     * Works only with *text/plain* MIME type.
     * 
     * @param emlFile a file in the EML format
     * @return [Content] object
     * @throws ParseException if the email does not contain text/plain part
     */
    fun parse(emlFile: File): Content {
        val msg: MimeMessage = getMimeMessage(emlFile)
        val emailText: String = getEmailText(msg)
        return parse(emailText.lines(), containInReplyToHeader(msg))
    }

    /**
     * Parse given list of strings to separate a quote from the useful content.
     * Works only with *text/plain* MIME type.
     * 
     * *hasInReplyToEMLHeader* defines if email contains *In-Reply-To* header 
     * or *References* header. If set to true then weakened criteria are used.
     *
     * @param lines list of strings with email content. Strings must be represented as a plain text.
     * @param hasInReplyToEMLHeader defines if email contains In-Reply-To header or References header. Default = true 
     * @return [Content] object
     * @see containInReplyToHeader
     */
    fun parse(lines: List<String>, hasInReplyToEMLHeader: Boolean = true): Content {
        this.lines = lines

        val matchingLines = this.quoteMarkFeature.matchLines(this.lines)
        val headerLinesIndexes = this.quoteHeaderLinesParser.parse(this.lines, matchingLines)
        val quoteMarkIndex =
                // This condition means: for EMLs without In-Reply-To header or References header
                // search for quotation marks(>) only if quoteHeaderLines is null. It works well for
                // the test data, but it is weird.. because it skips quotation marks most of the time.
                //
                // As alternative this condition may be deleted, but it works
                // worse with some cases...
                if (!hasInReplyToEMLHeader && headerLinesIndexes != null) {
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
                        matchingLines = matchingLines
                )
        }

        // Without this condition smart cast doesn't work.
        if (headerLinesIndexes == null || quoteMarkIndex == null)
            throw IllegalStateException("Never gets here")

        val startHeaderLinesIndex = headerLinesIndexes.first
        val endHeaderLineIndex = headerLinesIndexes.second

        val relation = getRelation(
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
            return this.createContent(
                    quoteMarkIndex,
                    matchingLines = matchingLines
            )
        } else {
            return this.createContent(startHeaderLinesIndex, endHeaderLineIndex + 1, matchingLines)
        }
    }

    private fun getContentQuoteMarkFirstCase(startHeaderLinesIndex: Int, endHeaderLineIndex: Int, quoteMarkIndex: Int,
                                             matchingLines: List<QuoteMarkMatchingResult>): Content {

        val isTextBetween = isTextBetween(quoteMarkIndex, startHeaderLinesIndex, matchingLines)
        val isQuotedTextBetween = isQuotedTextBetween(quoteMarkIndex, startHeaderLinesIndex, matchingLines)

        if (isTextBetween) {

            var i = startHeaderLinesIndex
            while (i > 0 && matchingLines[i - 1] != QuoteMarkMatchingResult.NOT_EMPTY)
                --i
            if (i == 0 || matchingLines.subList(i, this.lines.size).any() { it.hasQuoteMark() }) {
                val msg = "Relation = ${Relation.QUOTE_MARK_FIRST}. Found quoteMark(>), but in the lines after ${i - 1} must not be any quote mark!"
                throw IllegalStateException(msg)
            }

            return this.createContent(startHeaderLinesIndex, endHeaderLineIndex + 1, matchingLines)

        } else if (isQuotedTextBetween) {
            return this.createContent(
                    quoteMarkIndex,
                    matchingLines = matchingLines
            )
        }

        var startIndex = startHeaderLinesIndex
        if (matchingLines.subList(startHeaderLinesIndex, endHeaderLineIndex + 1).all { it.hasQuoteMark() }) {
            while (startIndex > 0 && matchingLines[startIndex - 1].hasQuoteMark()) {
                --startIndex
            }
        }

        return this.createContent(startIndex, endHeaderLineIndex + 1, matchingLines)
    }


    private fun createContent(fromIndex: Int,
                              toIndex: Int = fromIndex,
                              matchingLines: List<QuoteMarkMatchingResult>): Content {
        this.deleteQuoteMarks(fromIndex, toIndex, matchingLines)

        return if (!this.recursive) {
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
}
