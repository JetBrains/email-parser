package practice.email.parser

enum class QuotesHeaderSuggestionsRegEx(val regex: Regex) {
    DATE(
            Regex("(.*\\s)?((([0-3]?[0-9][\\.,]{0,2}\\s+)(\\S+\\s+)?(\\S+\\s+)?(20\\d\\d[\\.,]{0,2}))|" +  // full
                    "((20\\d\\d[\\.,]{0,2}\\s+)(\\S+\\s+)?(\\S+\\s+)?([0-3]?[0-9][\\.,]{0,2}))|" +

                    "((([0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2})|" +  // short
                    "((?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]))[,\\.]?:?" +
                    "))(\\s.*)?")
    ),
    TIME(
            Regex("(.*\\s)?(([01]?[0-9]|2[0-3]):([0-5][0-9])(:[0-5][0-9])?[,\\.]?:?)(\\s.*)?")
    ),
    EMAIL(
            Regex("(.*\\s)?\\S+@\\S+(\\s.*)?")
    ),
    COLON(
            Regex("(.*\\s)?.*:(\\s*)?")
    ),
    MIDDLE_COLON(
            Regex(".*\\S+\\s*:\\s+\\S+.*")
    )
}

object QuotesHeaderSuggestions {
    val COUNT = 4
    val SUFFICIENT_COUNT = 2

    object idx {
        val DATE_YEAR = 0
        val TIME = 1
        val EMAIL = 2
        val COLON = 3
    }

    // For multilines and FWD headers
    private val MIDDLE_COLON_LINES_COUNT = 4
    private var middleColonCount = 0
    private val middleColonLineIndex = IntArray(MIDDLE_COLON_LINES_COUNT) { -1 }
    // ------

    private var suggestionsFound = 0
    private val suggestions = BooleanArray(COUNT) { false }
    private val suggestionsLineIndex = IntArray(COUNT) { -1 }

    fun getQuoteHeader(content: String): List<String>? {
        this.suggestionsFound = 0
        this.suggestions.fill(false)
        this.suggestionsLineIndex.fill(-1)

        val lines = content.lines()
        lines.forEachIndexed { i, s ->
            updateSuggestions(i, s)
            if (this.suggestionsFound >= SUFFICIENT_COUNT) {
                val (from, to, header_lines) = getHeader(lines)
                return@getQuoteHeader header_lines
            }
        }
        return null
    }

    fun getQuoteHeaderWithIndexes(content: String): Triple<Int, Int, List<String>?> {
        this.suggestionsFound = 0
        this.suggestions.fill(false)
        this.suggestionsLineIndex.fill(-1)

        val lines = content.lines()
        lines.forEachIndexed { i, s ->
            updateSuggestions(i, s)
            if (this.suggestionsFound >= SUFFICIENT_COUNT) {
                return@getQuoteHeaderWithIndexes getHeader(lines)
            }
        }
        return Triple(-1, -1, null)
    }

    fun getQuoteHeaderLine(content: String): String? =
            getQuoteHeader(content)?.filter { it != "" }?.joinToString(separator = " ")

    fun getPreprocessedQuoteHeaderLine(content: String): String? {
        val header = getQuoteHeader(content)
        return if (header != null ) preprocess(header) else null
    }

    private fun updateSuggestions(lineIndex: Int, line: String) {
        var updated = update(lineIndex, line, idx.DATE_YEAR, QuotesHeaderSuggestionsRegEx.DATE.regex)
        updated = update(lineIndex, line, idx.TIME, QuotesHeaderSuggestionsRegEx.TIME.regex) || updated
        updated = update(lineIndex, line, idx.EMAIL, QuotesHeaderSuggestionsRegEx.EMAIL.regex) || updated
        updated = update(lineIndex, line, idx.COLON, QuotesHeaderSuggestionsRegEx.COLON.regex) || updated

        updated = updateMiddleColon(lineIndex, line) || updated

        if (updated) {
            resetSuggestions(lineIndex = lineIndex)
        } else {
            resetSuggestions(all = true)
        }
    }

    private fun update(lineIndex: Int, line: String, suggestionIndex: Int, regex: Regex): Boolean {
        if (line.matches(regex)) {
            if (!this.suggestions[suggestionIndex]) {
                this.suggestions[suggestionIndex] = true
                this.suggestionsFound++
            }
            this.suggestionsLineIndex[suggestionIndex] = lineIndex
            return true
        }
        return false
    }

    //    TODO didn't find middle_comma in email 40
    private fun updateMiddleColon(lineIndex: Int, line: String, shouldReset: Boolean = true): Boolean {
        if (line.matches(QuotesHeaderSuggestionsRegEx.MIDDLE_COLON.regex) &&
                (middleColonCount == 0 || lineIndex - 1 == middleColonLineIndex[middleColonCount - 1])) {
            if (middleColonCount == MIDDLE_COLON_LINES_COUNT) {
                middleColonLineIndex.forEachIndexed { i, line ->
                    if (i < middleColonCount - 1)
                        middleColonLineIndex[i] = middleColonLineIndex[i + 1]
                }
                middleColonLineIndex[middleColonCount - 1] = lineIndex
            } else {
                middleColonLineIndex[middleColonCount++] = lineIndex
            }
            return true
        } else {
            if (shouldReset) {
                middleColonCount = 0
            }
            return false
        }
    }

    private fun resetSuggestions(lineIndex: Int = 0, all: Boolean = false) {
        for (i in 0..COUNT - 1) {
            if (this.suggestions[i] && (all || lineIndex - this.suggestionsLineIndex[i] > 2)) {
                this.suggestionsLineIndex[i] = -1
                this.suggestions[i] = false
                this.suggestionsFound--
            }
        }
    }

    private fun getHeader(lines: List<String>): Triple<Int, Int, List<String>> {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        for (i in 0..COUNT - 1) {
            if (this.suggestions[i]) {
                fromIndex = Math.min(fromIndex, this.suggestionsLineIndex[i])
                toIndex = Math.max(toIndex, this.suggestionsLineIndex[i])
            }
        }

        var additionalLine = 0

        // If sufficient count of suggestions had been found in one or two lines
        // try to check the last suggestion in the following line.
        val lastSuggestionIdx = this.suggestions.indexOfFirst { !it }
        if (lastSuggestionIdx != -1 && // One suggestion is not found.
                toIndex < lines.size - 1 && // There is the following line to check.
                toIndex - fromIndex < 2) {  // Found suggestions are placed in less than 3 lines.
            val updated = update(
                    toIndex + 1,
                    lines[toIndex + 1],
                    lastSuggestionIdx,
                    QuotesHeaderSuggestionsRegEx.values()[lastSuggestionIdx].regex
            )

            // Check if this line contains middle colon
            updateMiddleColon(toIndex + 1, lines[toIndex + 1])
            additionalLine = 1

            if (updated) {
                toIndex++
            }
        }

        // Try to check if there is a multiline header or FWD
        if (middleColonCount >= toIndex - fromIndex + 1) {
            var cnt = MIDDLE_COLON_LINES_COUNT - middleColonCount
            var lineIdx = toIndex + 1 + additionalLine
            while (cnt > 0 && lineIdx < lines.size) {

                val updated = updateMiddleColon(lineIdx, lines[lineIdx], shouldReset = false)
                if (!updated) {
                    break
                }

                lineIdx++
                cnt--
            }

            if (middleColonLineIndex[0] <= fromIndex && middleColonLineIndex[middleColonCount - 1] >= toIndex) {
                fromIndex = middleColonLineIndex[0]
                toIndex = middleColonLineIndex[middleColonCount - 1]
            }
        }

        return Triple(fromIndex, toIndex, lines.filterIndexed { i, s -> i >= fromIndex && i <= toIndex })
    }
}

