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

package quoteParser.features

enum class QuoteMarkMatchingResult {
    V_EMPTY,
    V_NOT_EMPTY,
    EMPTY,
    NOT_EMPTY;

    fun hasQuoteMark() = this == V_EMPTY || this == V_NOT_EMPTY
    fun isTextWithoutQuoteMark() = this == NOT_EMPTY
}

class QuoteMarkFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "QUOTE_MARK"

    override fun getRegex(): Regex {
        // Regex for matching greater-than(>) symbol in the beginning of the line.
        return Regex("${this.whitespace}*>.*")
    }

    fun matchLines(lines: List<String>): List<QuoteMarkMatchingResult> {
        return lines.map {
            val matchesQuoteMark = this.matches(it)
            val containText: Boolean
            if (matchesQuoteMark) {
                val quoteMarkIndex = it.indexOfFirst { it == '>' }
                if (quoteMarkIndex == -1) {
                    throw IllegalStateException("Line \"$it\" must contain '>' symbol, but it is not.")
                }
                containText = !it.substring(quoteMarkIndex + 1).trim().isEmpty()
            } else {
                containText = !it.trim().isEmpty()
            }
            return@map when {
                matchesQuoteMark && containText -> QuoteMarkMatchingResult.V_NOT_EMPTY
                matchesQuoteMark && !containText -> QuoteMarkMatchingResult.V_EMPTY
                !matchesQuoteMark && containText -> QuoteMarkMatchingResult.NOT_EMPTY
                else -> QuoteMarkMatchingResult.EMPTY
            }
        }
    }
}


/**
 * It is a common case when header of the quote is followed
 * by the quote marks(>).
 */
internal fun checkQuoteMarkSuggestion(endIdx: Int, lines: List<String>,
                             matchedLinesQuoteMark: List<QuoteMarkMatchingResult>): Boolean {
    var idx = endIdx + 1
    while (idx < lines.size) {
        if (matchedLinesQuoteMark[idx].isTextWithoutQuoteMark()) {
            return false
        }
        if (matchedLinesQuoteMark[idx].hasQuoteMark()) {
            return true
        }
        idx++
    }
    return false
}