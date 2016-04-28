package practice.email.parser

enum class QuotesHeaderSuggestionsRegEx(val regex: Regex) {
    DATE_YEAR(
            Regex("(.*\\s)?((20\\d\\d,?)|(${TokenRegEx.DATE.regex})|(${TokenRegEx.DATE_REVERSE.regex}))(\\s.*)?")
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
        suggestionsFound = 0
        suggestions.fill(false)
        suggestionsLineIndex.fill(-1)

        val lines = content.lines()
        lines.forEachIndexed { i, s ->
            updateSuggestions(i, s)
            if (suggestionsFound >= SUFFICIENT_COUNT) {
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

        for (i in 0..COUNT - 1) {
            if (suggestions[i] && (lineIndex - suggestionsLineIndex[i] > 2)) {
                suggestionsLineIndex[i] = -1
                suggestions[i] = false
                suggestionsFound--
            }
        }
    }

    private fun update(lineIndex: Int, line: String, suggestionIndex: Int, regex: Regex) {
        if (line.matches(regex)) {
            if (!suggestions[suggestionIndex]) {
                suggestions[suggestionIndex] = true
                suggestionsFound++
            }
            suggestionsLineIndex[suggestionIndex] = lineIndex
        }
    }

    private fun getHeader(lines: List<String>): String {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        for (i in 0..COUNT - 1) {
            if (suggestions[i]) {
                fromIndex = Math.min(fromIndex, suggestionsLineIndex[i])
                toIndex = Math.max(toIndex, suggestionsLineIndex[i])
            }
        }

        return lines
                .filterIndexed { i, s -> i >= fromIndex && i <= toIndex }
                .joinToString(separator = "")
    }
}

