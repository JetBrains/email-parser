package quoteParser.features

import org.intellij.lang.annotations.Language

/**
 * Created by Pavel.Zhuk on 24.08.2016.
 */
class QuoteMarkFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "QUOTE_MARK"

    override fun getRegex(): Regex {
        // Regex for matching greater-than(>) symbol in the start of the line.
        return Regex("[\\s\\p{C}\\p{Z}]*>.*")
    }

    fun matchLines(lines: List<String>): List<Boolean> {
        return lines.map { this.matches(it) }
    }

    companion object {
        fun getQuoteIndex(matchesLines: List<Boolean>, lines: List<String>): Int? {
            val startQuoteBlockIndex: Int
            var endQuoteBlockIndex: Int = matchesLines.size - 1
            while (endQuoteBlockIndex > -1 &&
                    !matchesLines[endQuoteBlockIndex]) {
                endQuoteBlockIndex--
            }

            if (endQuoteBlockIndex == -1) {
                return null
            } else {
                var matchesGTIndex = endQuoteBlockIndex
                var lineIndex = endQuoteBlockIndex
                while (lineIndex > -1) {
                    val isEmpty = lines[lineIndex].trim().isEmpty()
                    val matchesGT = matchesLines[lineIndex]
                    if (matchesGT) {
                        matchesGTIndex = lineIndex
                    }
                    if (!isEmpty && !matchesGT) {
                        break
                    }
                    lineIndex--
                }
                startQuoteBlockIndex = matchesGTIndex
            }
            // TODO delete that after adding IN-REPLY-TO header processing. One-line quotes are exists.
            return if (endQuoteBlockIndex - startQuoteBlockIndex == 0)
                null
            else
                startQuoteBlockIndex
        }
    }
}