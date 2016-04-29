package practice.email.parser

enum class QuotesHeaderSuggestionsRegEx(val regex: Regex) {
    DATE_YEAR(
            Regex("(.*\\s)?((20\\d\\d,?)|(${TokenRegEx.DATE.regex}))(\\s.*)?")
    ),
    TIME(
            Regex("(.*\\s)?${TokenRegEx.TIME.regex}(\\s.*)?")
    ),
    EMAIL(
            Regex("(.*\\s)?${TokenRegEx.EMAIL.regex}(\\s.*)?")
    ),
    COLUMN(
            Regex("(.*\\s)?.*:(\\s.*)?")
    )
}

object QuotesHeaderSuggestions {
    val COUNT = 4
    val SUFFICIENT_COUNT = 3

    object idx {
        val DATE_YEAR = 0
        val TIME = 1
        val EMAIL = 2
        val COLUMN = 3
    }

    private var suggestionsFound = 0
    private val suggestions = BooleanArray(COUNT) { false }
    private val suggestionsLineIndex = IntArray(COUNT) { -1 }

    fun getQuoteHeader(content: String): String? {
        this.suggestionsFound = 0
        this.suggestions.fill(false)
        this.suggestionsLineIndex.fill(-1)

        val lines = content.lines()
        lines.forEachIndexed { i, s ->
            updateSuggestions(i, s)
            if (this.suggestionsFound >= SUFFICIENT_COUNT) {
                return@getQuoteHeader getHeader(lines)
            }
        }
        return null
    }

    private fun updateSuggestions(lineIndex: Int, line: String) {
        update(lineIndex, line, idx.DATE_YEAR, QuotesHeaderSuggestionsRegEx.DATE_YEAR.regex)
        update(lineIndex, line, idx.TIME, QuotesHeaderSuggestionsRegEx.TIME.regex)
        update(lineIndex, line, idx.EMAIL, QuotesHeaderSuggestionsRegEx.EMAIL.regex)
        update(lineIndex, line, idx.COLUMN, QuotesHeaderSuggestionsRegEx.COLUMN.regex)

        resetOldSuggestions(lineIndex)
    }

    private fun update(lineIndex: Int, line: String, suggestionIndex: Int, regex: Regex) {
        if (line.matches(regex)) {
            if (!this.suggestions[suggestionIndex]) {
                this.suggestions[suggestionIndex] = true
                this.suggestionsFound++
            }
            this.suggestionsLineIndex[suggestionIndex] = lineIndex
        }
    }

    private fun resetOldSuggestions(lineIndex: Int) {
        for (i in 0..COUNT - 1) {
            if (this.suggestions[i] && (lineIndex - this.suggestionsLineIndex[i] > 2)) {
                this.suggestionsLineIndex[i] = -1
                this.suggestions[i] = false
                this.suggestionsFound--
            }
        }
    }
    
    private fun getHeader(lines: List<String>): String {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        for (i in 0..COUNT - 1) {
            if (this.suggestions[i]) {
                fromIndex = Math.min(fromIndex, this.suggestionsLineIndex[i])
                toIndex = Math.max(toIndex, this.suggestionsLineIndex[i])
            }
        }

        return lines
                .filterIndexed { i, s -> i >= fromIndex && i <= toIndex }
                .joinToString(separator = "")
    }
}

